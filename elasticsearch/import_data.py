import requests
import os


##### CONFIG #####

# path to unzipped `spotify-podcasts-2020` folder
SPOTIFY_PODCASTS_2020 = "/Users/mebn/Desktop/podcasts-no-audio-13GB/spotify-podcasts-2020/"

# folder inside `SPOTIFY_PODCASTS_2020` containing all the transcripts (select one of these)
DATA_SET_PATH = "podcasts-transcripts-summarization-testset" # ~1 GB
# DATA_SET_PATH = "podcasts-transcripts" # ~100 GB

# do not modify
DATA_PATH = SPOTIFY_PODCASTS_2020 + DATA_SET_PATH

# the index to save documents
INDEX = "podcasts"

# url of elasticsearch from docker, most likely "http://localhost:9200"
ELASTICSEARCH_URL = "http://localhost:9200"

##### CONFIG END #####


# go throught every file in every subdir and PUT json file to elasticsearch with filename as id
def main():
    count = 0
    for root, dirs, files in os.walk(DATA_PATH):
        for name in files:
            if name.endswith((".json")):
                with open(f"{root}/{name}", "rb") as f:
                    data = f.read()
                    id = os.path.splitext(name)[0]
                    res = insert(data, id)
                    print(id, res)
                    count += 1
    print(count)


def insert(data, id):
    headers = {'Accept': 'application/json', 'Content-Type': 'application/json'}
    url = f"{ELASTICSEARCH_URL}/{INDEX}/_doc/{id}"
    return requests.put(url, data=data, headers=headers)


main()