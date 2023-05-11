# DD2477_project

## How to run
TLDR; follow [this](#start-the-search-engine-project).

### Indexing
Indexing is already done. Note, indexing takes a loooong time.

You can get the complete indexing [here](https://hub.docker.com/r/mebn/elasticsearch_podcast) or do it yourself.

1. Download the Spotify data files
1. have a local Elasticsearch docker container up and running
1. Update information inside the CONFIG block in `index.py`
1. Run `python index.py`

### Start the search engine project
1. Make sure you have elasticsearch installed and started.
2. Modify the ip and port of Elasticsearch you are using in podcast/src/main/java/org/engine/Engine.java. 
3. Open podcast as a maven project in Intellij IDEA, run podcast/src/main/java/org/interaction/MainFrame.java, you will see the interface and you can try the search engine. If you have some problems when running it, you might find this[these steps](https://stackoverflow.com/a/52675252) help. Or you can use maven to compile and build it.
