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

### Start the search engine project
Make sure you have elasticsearch installed and modify the ip and port you are using in podcast/src/main/java/org/engine/Engine.java. Open podcast as a maven project in Intellij IDEA, run podcast/src/main/java/org/interaction/MainFrame.java, you will see the interface and you can try the search engine. If you have some problems when running it, you might find this[these steps](https://stackoverflow.com/a/52675252) help.
