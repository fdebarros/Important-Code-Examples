# This dockerfile don't work, DON'T TRY TO BUILD IT!
# The only propouse of this file is to show a example of a lot of dockerfile features.
# URL of the examples --> https://docs.docker.com/develop/develop-images/dockerfile_best-practices/
# It's strongly recommend look for this URL above for a best overview.

# Simple example
FROM ubuntu:18.04
COPY . /app
RUN make /app
CMD python /app/app.py

# Build context
mkdir myproject && cd myproject
echo "hello" > hello
echo -e "FROM busybox\nCOPY /hello /\nRUN cat /hello" > Dockerfile
docker build -t helloapp:v1 .

mkdir -p dockerfiles context
mv Dockerfile dockerfiles && mv hello context
docker build --no-cache -t helloapp:v2 -f dockerfiles/Dockerfile context

# Create a directory to work in
mkdir example
cd example

# Create an example file
touch somefile.txt

# Multi-line arguments
RUN apt-get update && apt-get install -y \
  bzr \
  cvs \
  git \
  mercurial \
  subversion

# Using label
LABEL com.example.version="0.0.1-beta"
LABEL vendor1="ACME Incorporated"
LABEL vendor2=ZENITH\ Incorporated

# Set multiple labels on one line
LABEL com.example.version="0.0.1-beta" com.example.release-date="2015-02-12"

# Set multiple labels at once, using line-continuation characters to break long lines
LABEL vendor=ACME\ Incorporated \
      com.example.is-beta= \
      com.example.is-production="" \
      com.example.version="0.0.1-beta" \
      com.example.release-date="2015-02-12"
      
# apt-get Example
RUN apt-get update && apt-get install -y \
    package-bar \
    package-baz \
    package-foo
    
# same as last but separated
RUN apt-get update
RUN apt-get install -y curl

# Declaring ENV Vars
ENV PG_MAJOR 9.3
ENV PG_VERSION 9.3.4
RUN curl -SL http://example.com/postgres-$PG_VERSION.tar.xz | tar -xJC /usr/src/postgress && …
ENV PATH /usr/local/postgres-$PG_MAJOR/bin:$PATH

# Using ENV Vars
FROM alpine
ENV ADMIN_USER="mark"
RUN echo $ADMIN_USER > ./mark
RUN unset ADMIN_USER

# Exporting ENV vars from files

COPY FILE /tmp/directory/FILE
ENV FILE $VAR_FILE

# Using vars on URL and Downloading file from artifactory

COPY PROJECT-VERSION /tmp/directory/PROJECT_VERSION
ARG PROJECT_VERSION
ENV PROJECT_VERSION $PROJECT_VERSION
RUN curl -k -u user:password -O https://artifactory.com/artifactory/list/repository-name/com/org/app/artifact/$PROJECT_VERSION/artifact-$PROJECT_VERSION.jar

# Using ENV vars for the user and password  for the same example

COPY PROJECT-VERSION /tmp/directory/PROJECT_VERSION
ARG PROJECT_VERSION
ENV PROJECT_VERSION $PROJECT_VERSION

COPY artifactory_user /tmp/directory/artifactory_user
COPY artifactory_key /tmp/directory/artifactory_key

RUN export ARTIFACTORY_USER=$(cat artifactory_user) \
    && export ARTIFACTORY_API_KEY=$(cat artifactory_key)

RUN curl -k -u ${ARTIFACTORY_USER}:${ARTIFACTORY_API_KEY} -O https://artifactory.com/artifactory/list/repository-name/com/org/app/artifact/$PROJECT_VERSION/artifact-$PROJECT_VERSION.jar

