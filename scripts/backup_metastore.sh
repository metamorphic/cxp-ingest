#!/bin/bash

datestamp=$(date +"%Y%m%d")

# delete files older than 10 days ago
find /data/backups/metastore/cxpdev_2*.sql -mtime +10 -type f -delete

/usr/local/pgsql/bin/pg_dump cxpdev -U mark > /data/backups/metastore/cxpdev_$datestamp.sql