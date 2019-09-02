FROM debian

COPY . /app

WORKDIR /app

RUN export GHE_TOKEN=$(cat mytoken)

FROM ubuntu

WORKDIR /tmp/deploy

STOPSIGNAL SIGTERM

RUN apt update \
	&& apt install -y gcc s3270 uwsgi build-essential python-dev \
	&& create-user app \
	&& apt -y --no-install-recommends install curl \
	&& get-deps -r tianon/gosu -f gosu-amd64 -s gh -v 1.10


COPY --from=builder /app/dist/app*.deb .

RUN dpkg -i app*.deb

COPY entrypoint.sh /usr/local/bin/entrypoint.sh

RUN cleanup -p curl -d "/tmp/deploy"

WORKDIR /home/app

ENTRYPOINT ["entrypoint.sh"]

EXPOSE 8000
