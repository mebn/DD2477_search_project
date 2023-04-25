import requests
import os
import csv
import json


##### CONFIG #####

# path to unzipped `spotify-podcasts-2020` folder
SPOTIFY_PODCASTS_2020 = "/Users/mebn/Desktop/podcasts-no-audio-13GB/spotify-podcasts-2020/"

# folder inside `SPOTIFY_PODCASTS_2020` containing all the transcripts (select one of these)
PODCASTS_SET = "podcasts-transcripts-summarization-testset" # ~1 GB
# PODCASTS_SET = "podcasts-transcripts" # ~100 GB

# file containing metdata (show name, url, etc) (just a file)
METADATA_SET = "metadata-summarization-testset.tsv" # testset
# METADATA_SET = "" # main big set

# the index to save documents
PODCASTS_INDEX = "podcasts"
EPISODES_INDEX = "episodes"
METADATA_INDEX = "metadata"

# url of elasticsearch from docker, most likely "http://localhost:9200"
ELASTICSEARCH_URL = "http://localhost:9200"

# do not modify
PODCASTS_PATH = SPOTIFY_PODCASTS_2020 + PODCASTS_SET
METADATA_PATH = SPOTIFY_PODCASTS_2020 + METADATA_SET

##### CONFIG END #####


def main():
    index_metadata()
    index_episodes()


# podcast episode id as _doc id
def index_metadata():
    with open(METADATA_PATH) as fd:
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
                            i += 1
                            count += 1
                            
                    print(id, res)

    print(f"Indexed {count} documents")


def insert(index, id, data):
    headers = {"Accept": "application/json", "Content-Type": "application/json"}
    url = f"{ELASTICSEARCH_URL}/{index}/_doc/{id}"
    return requests.put(url, data=data, headers=headers)


if __name__ == "__main__":
    main()