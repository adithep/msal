#!/bin/bash
CUR_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
JOBS_DB_POSTING_JSON=$(cat "$CUR_DIR"/jobsDBPosting.json)

JOBS_DB_POSTING_JSON_NEWLINE=$(printf "%q\n" "${JOBS_DB_POSTING_JSON}")

prefix="$'"
suffix="'"
JOBS_DB_POSTING_JSON_NEWLINE=${JOBS_DB_POSTING_JSON_NEWLINE#$prefix}
JOBS_DB_POSTING_JSON_NEWLINE=${JOBS_DB_POSTING_JSON_NEWLINE%$suffix}

JOBS_DB_POSTING_JSON_STRING=${JOBS_DB_POSTING_JSON_NEWLINE//\"/\\\"}


echo 'posting'
echo '{"schema": "'"$JOBS_DB_POSTING_JSON"'"}'

curl -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" \
    --data '{"schema": "'"$JOBS_DB_POSTING_JSON_STRING"'"}' \
    http://schemaRegistry:8081/subjects/jobsDBPosting-value/versions