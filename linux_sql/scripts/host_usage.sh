#!/bin/bash

#setup CLI arguments
psql_host=$1
psql_port=$2
db_name=$3
psql_user=$4
psql_password=$5

#check # of args
if [ "$#" -ne 5 ]; then
  echo "Illegal number of parameters"
  exit 1
fi

#save vmstat and hostname to variables
vmstat_mb=$(vmstat --unit M)
hostname=$(hostname -f)

#retrieve usage info
memory_free=$(echo "$vmstat_mb" | tail -n1 | awk '{print $4}' | xargs)
cpu_idle=$(echo "$vmstat_mb" | tail -n1 | awk '{print $15}' | xargs)
cpu_kernel=$(echo "$vmstat_mb" | tail -n1 | awk '{print $14}' | xargs)
disk_io=$(vmstat -d | tail -n1 | awk '{print $10}' | xargs)
disk_available=$(df -BM / | tail -n1 | awk '{print $4}' | sed s/M// | xargs)

#current time in '2019-11-26 14:40:19' UTC format
timestamp=$(vmstat -t | tail -n1 | awk '{print $18 " " $19}')

#subquery - find matching id in host_info table
host_id="(SELECT id FROM host_info WHERE hostname='$hostname')"

#PSQL command: inserts usage data into host_usage table
insert_stmt="INSERT INTO host_usage(timestamp, host_id, memory_free, cpu_idle, cpu_kernel, disk_io, disk_available) \
VALUES ('$timestamp', $host_id, '$memory_free', '$cpu_idle', '$cpu_kernel', '$disk_io', '$disk_available')"

#set up env var
export PGPASSWORD=$psql_password

#psql command with insert statement
psql -h $psql_host -p $psql_port -U $psql_user -d $db_name -c "$insert_stmt"

exit $?