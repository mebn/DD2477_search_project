import requests
import os
import csv
import json


##### CONFIG #####

# path to unzipped `spotify-podcasts-2020` folder
SPOTIFY_PODCASTS_2020 = "C:/Users/mebn/Desktop/spotify-podcasts-2020/"

# folder inside `SPOTIFY_PODCASTS_2020` containing all the transcripts (select one of these)
# PODCASTS_SET = "podcasts-transcripts-summarization-testset" # ~1 GB
PODCASTS_SET = "podcasts-transcripts" # ~100 GB

# file containing metdata (show name, url, etc) (just a file)
# METADATA_SET = "metadata-summarization-testset.tsv" # testset
METADATA_SET = "metadata.tsv" # main big set

# the index to save documents
EPISODES_1MIN_INDEX = "episodes_1min"
EPISODES_2MIN_INDEX = "episodes_2min"
EPISODES_INDEX = "episodes"
METADATA_INDEX = "metadata"

# url of elasticsearch from docker, most likely "http://localhost:9200"
ELASTICSEARCH_URL = "http://localhost:9200"

# length of indexed transcript
TIMEBLOCK = 120.0 # in seconds

# do not modify
PODCASTS_PATH = SPOTIFY_PODCASTS_2020 + PODCASTS_SET
METADATA_PATH = SPOTIFY_PODCASTS_2020 + METADATA_SET

##### CONFIG END #####


def main():
    print("started")
    
    # index_metadata()
    # index_episodes()
    index_xmin_episodes()
    
    print("ended")


# podcast episode id as _doc id
def index_metadata():
    with open(METADATA_PATH, encoding="utf8") as fd:
        rd = csv.reader(fd, delimiter="\t", quotechar='"')
        titles = next(rd)
        count = 0
        for row in rd:
            data = {}
            for x, y in zip(titles, row):
                data[x] = y
            id = data["episode_filename_prefix"]
            res = insert(METADATA_INDEX, id, json.dumps(data))
            if count % 100 == 0:
                print(id, res)
            count += 1
    print(count)


def index_xmin_episodes():
    count = count_files = 0
    
    for root, dirs, files in os.walk(PODCASTS_PATH):
        for name in files:
            if name.endswith((".json")):
                count_files += 1
                with open(f"{root}/{name}", "r", encoding="utf8") as f:
                    # print(f"{root}/{name}")
                    data = json.loads(f.read())
                    id = os.path.splitext(name)[0]

                    words = []
                    for alt in data["results"]:
                        alt = alt["alternatives"][0]
                        if "transcript" in alt:
                            words.extend(alt["words"])
                    
                    i = left = right = 0
                    go_back = []

                    while right < len(words):
                        word_obj = words[right]
                        starttime = float(word_obj["startTime"][:-1])
                        endtime = float(word_obj["endTime"][:-1])
                        go_back.append(starttime)

                        if endtime > (i + 1) * TIMEBLOCK - i * 60.0:
                            words_slice = words[left:right]
                            transcript = " ".join([x["word"] for x in words_slice])
                            data = { "transcript": transcript, "words": words_slice }

                            res = insert(EPISODES_2MIN_INDEX, f"{id}_{i}", json.dumps(data))
                            if res.status_code not in [200, 201]: print(id, res.status_code)

                            # go back to entry closest to 60s
                            closest_to = float(words[right-1]["endTime"][:-1]) - 60.0
                            for j, st in enumerate(go_back):
                                if st >= closest_to:
                                    left += j
                                    right = left
                                    break
                            go_back.clear()
                                    
                            count += 1
                            i += 1
                        else:
                            right += 1

                    # handle last section
                    if left < len(words):
                        words_slice = words[left:]
                        transcript = " ".join([x["word"] for x in words_slice])
                        data = { "transcript": transcript, "words": words_slice }
                        res = insert(EPISODES_2MIN_INDEX, f"{id}_{i}", json.dumps(data))
                        if res.status_code not in [200, 201]: print(id, res.status_code)
                        count += 1
                
                if count_files % 100 == 0:
                    print(f"{root}/{name}")

    print(f"Indexed {count} documents")


def index_episodes():
    count = 0
    for root, dirs, files in os.walk(PODCASTS_PATH):
        for name in files:
            if name.endswith((".json")):
                with open(f"{root}/{name}", "r") as f:
                    data = json.loads(f.read())
                    id = os.path.splitext(name)[0]

                    # index each ~30s transcript into a seperate document
                    # and skip entries with no transcripts
                    i = 0
                    for alt in data["results"]:
                        alt = alt["alternatives"][0]

                        if "transcript" in alt:
                            res = insert(EPISODES_INDEX, f"{id}_{i}", json.dumps(alt))
                            if res.status_code not in [200, 201]: print(id, res.status_code)
                            i += 1
                            count += 1
                if count % 1000 == 0:
                    print(f"{root}/{name}")

    print(f"Indexed {count} documents")


def insert(index, id, data):
    headers = {"Accept": "application/json", "Content-Type": "application/json"}
    url = f"{ELASTICSEARCH_URL}/{index}/_doc/{id}"
    return requests.put(url, data=data, headers=headers)


if __name__ == "__main__":
    main()