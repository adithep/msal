#!/bin/bash
CUR_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
mongo mongo:27017 "$CUR_DIR"/create-jobsdb-db.js