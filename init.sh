#!/bin/bash
docker-compose up -d mongo
docker-compose up schemaRegistry
docker-compose up -d connect-cluster
#wait for it to start

docker-compose up mongodb-create-db
docker-compose up post-mongodb-connect
docker-compose up postingProcessor
