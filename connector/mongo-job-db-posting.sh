#!/bin/bash
cat << EOF > MongoSinkConnector.json
{
  "name": "MongoSinkConnector",
  "config": {
    "connector.class": "com.datamountaineer.streamreactor.connect.mongodb.sink.MongoSinkConnector",
    "topics": "jobsDBPosting",
    "tasks.max": "1",
    "connect.mongo.database": "jobsDBPosting",
    "connect.mongo.connection": "mongodb://mongo:27017",
    "connect.mongo.sink.kcql": "INSERT INTO jobsDBPosting SELECT * FROM jobsDBPosting"
  }
}
EOF
curl -X POST -H "Content-Type: application/json" -H "Accept: application/json" -d @MongoSinkConnector.json http://connect-cluster:8083/connectors