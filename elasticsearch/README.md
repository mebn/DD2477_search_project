# Elasticsearch

## Docker image
<!--
### Commit changes to new docker image
You don't have to do this step, i've already done it.

1. get container id of elasticsearch: `docker ps -a` (eg `323a4f9ac5b3`)
2. commit: `docker commit <container id> <new image name>` (eg `docker commit 323a4f9ac5b3 elasticsearch_1gb`)

### Save image
You don't have to do this step, i've already done it.

Save the 1 gb elasticsearch database:
```
docker save -o elasticsearch_1gb.tar elasticsearch_1gb:latest
```

### Download and load image
Download `elasticsearch_1gb.tar.gz` or `elasticsearch_100gb.tar.gz`.

```
# For the 1 gb elasticsearch database
gunzip elasticsearch_1gb.tar.gz
docker load -i elasticsearch_1gb.tar

# For the 100 gb elasticsearch database
gunzip elasticsearch_100gb.tar.gz
docker load -i elasticsearch_100gb.tar
```

### Create and run container based on image
Only run this step once. To start/stop already existing containers, go to the next step.

```
# For the 1 gb elasticsearch database
docker run --name elasticsearch_1gb_container -p 9200:9200 -p 9300:9300 -t elasticsearch_1gb:latest

# For the 100 gb elasticsearch database
docker run --name elasticsearch_100gb_container -p 9200:9200 -p 9300:9300 -t elasticsearch_100gb:latest
```

### Start and stop container
```
# For the 1 gb elasticsearch database
# start
docker start elasticsearch_1gb_container
# stop
docker stop elasticsearch_1gb_container

# For the 100 gb elasticsearch database
# start
docker start elasticsearch_100gb_container
# stop
docker stop elasticsearch_100gb_container
```
-->

### Testset docker image
This is for the 1 gb elasticsearch database. Docker image is ~5gb. Run these commands:

```
docker pull mebn/elasticsearch_1gb
docker run --name elasticsearch_1gb_container -p 9200:9200 -p 9300:9300 -t mebn/elasticsearch_1gb:latest
```

To start and stop after running the commands above, run:
```
# start
docker start elasticsearch_1gb_container
# stop
docker stop elasticsearch_1gb_container
```

## Connect to elasticsearch locally
You can use postman, insomnia or something similar for testing.

### Get a specific document
GET request to `http://localhost:9200/episodes/_doc/4EXzSCiNj5DpQVavxZOW5Y_0`, where `_0` can be any number.

### Search
GET request to `http://localhost:9200/episodes/_search` with json body:

```json
{
  "query" : {
    "match" : {
      "transcript": "terrorist"
    }
  }
}
```