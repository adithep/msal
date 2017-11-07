FROM openjdk:8-jre

RUN URL='https://github.com/Yelp/dumb-init/releases/download/v1.2.0/dumb-init_1.2.0_amd64.deb'; \
FILE=$(mktemp); curl -L "$URL" -o $FILE && dpkg -i $FILE; rm $FILE

ENV SCALA_VERSION 2.12
ENV KAFKA_VERSION 0.10.2.1

ENV KAFKA_HOME /usr/local/kafka
ENV KAFKA_CONFIG $KAFKA_HOME/config
ENV KAFKA_DATA $KAFKA_HOME/data

RUN URL=http://www-us.apache.org/dist/kafka/"$KAFKA_VERSION"/kafka_"$SCALA_VERSION"-"$KAFKA_VERSION".tgz; \
SUM_URL=https://www-us.apache.org/dist/kafka/"$KAFKA_VERSION"/kafka_"$SCALA_VERSION"-"$KAFKA_VERSION".tgz.sha1; \
FILE=kafka_"$SCALA_VERSION"-"$KAFKA_VERSION".tgz; SUM_FILE=kafka_"$SCALA_VERSION"-"$KAFKA_VERSION".tgz.sha1; \
curl -L "$URL" -o $FILE && curl -L "$SUM_URL" -o $SUM_FILE && \
gpg --print-md sha1 $FILE | diff - $SUM_FILE && \
tar -C /usr/local --strip-components 1 -xf $FILE; \
rm $FILE && rm $SUM_FILE

EXPOSE 9092

RUN mkdir -p "$KAFKA_CONFIG"
RUN mkdir -p "$KAFKA_DATA"
WORKDIR $KAFKA_HOME
