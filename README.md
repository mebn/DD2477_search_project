# DD2477_project

## How to run
TLDR; follow [this](#gui).

### Indexing
Indexing is already done. No need to reindex since it takes a loooong time.

1. no, don't do this
1. Download the Spotify data files
1. have a local Elasticsearch docker container up and running
1. Update information inside the CONFIG block in `index.py`
1. Run `python index.py`

### GUI
Make sure you have elasticsearch installed and in your path. If using Intellij IDEA, you might have to follow [these steps](https://stackoverflow.com/a/52675252) first.
