#!/bin/bash
CUR_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
mongo mongo-jobs-writer:27017 "$CUR_DIR"/create-mongo-jobs-writer-db.js