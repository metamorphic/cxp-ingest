#!/bin/bash

datestamp=$(date +"%Y%m%d")

# delete files older than 10 days ago
find /data/backups/metastore/cxpdev_g*.sql -mtime +10 -type f -delete

pg_dump cxpdev -h 10.97.33.10 -U mark -ncxp > /data/backups/metastore/cxpdev_gp_$datestamp.sql