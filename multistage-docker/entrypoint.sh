#!/bin/bash

exec gosu app uwsgi --ini /etc/uwsgi/apps-enabled/app.ini
