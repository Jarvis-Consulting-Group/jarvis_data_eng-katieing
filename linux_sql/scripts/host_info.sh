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

#save needed variables
lscpu_out=$(lscpu)
hostname=$(hostname -f)

#retrieve hardware info
cpu_number=$(echo "$lscpu_out" | grep -E "^CPU\(s\):" | awk '{print $2}' | xargs)
cpu_architecture=$(echo "$lscpu_out" | grep -E "Architecture:" | awk '{print $2}' | xargs)
cpu_model=$(echo "$lscpu_out" | grep -E "Model name:" | awk '{$1=$2="";print}' | xargs)
cpu_mhz=$(echo "$lscpu_out" | grep -E "CPU MHz:" | awk '{print $NF}' | xargs)
l2_cache=$(echo "$lscpu_out" | grep -E "L2 cache:" | awk '{print $NF}' | sed s/K// | xargs)
total_mem=$(cat /proc/meminfo | grep -E "MemTotal:" | awk '{print $2}' | xargs)

#retrieve current time
timestamp=$(date "+%F %T")

#possible check if already exists in table. psql returns the error itself, prob not necessary

#psql command: inserts hardware data into host_info table
insert_stmt="INSERT INTO host_info(hostname, cpu_number, cpu_architecture, cpu_model, cpu_mhz, l2_cache, timestamp, total_mem) \
VALUES ('$hostname', '$cpu_number', '$cpu_architecture', '$cpu_model', '$cpu_mhz', '$l2_cache', '$timestamp', '$total_mem')"

#set up env var
export PGPASSWORD=$psql_password

#insert new row to database
psql -h $psql_host -p $psql_port -U $psql_user -d $db_name -c "$insert_stmt"

exit 0