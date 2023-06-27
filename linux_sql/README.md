# Linux Cluster Monitoring Agent

# Introduction
This project is a monitoring agent that stores data on hardware specifications and usage information. The monitoring 
agent is intended for use on a Linux cluster of nodes connected through a switch. 

The project utilizes Bash scripts for docker container creation and pulling data to be stored in PostgreSQL.


# Quick Start
1. Create a psql container using psql_docker.sh with username postgres.

```
./psql_docker.sh create postgres password 

#If the psql container has already been created, simply start the container.
./psql_docker.sh start
```
    
3. Create the host_agent database.

```psql -h localhost -p 5432 -U postgres -c "CREATE DATABASE host_agent;"```

4. Create the host_info and host_usage tables.

```psql -h localhost -U postgres -d host_agent -f sql/ddl.sql ```

5. Insert hardware specs data into the DB using host_info.sh

```./host_info.sh localhost 5432 host_agent postgres password ```

6. Insert hardware usage data into the DB using host_usage.sh

```./host_info.sh localhost 5432 host_agent postgres password```

7. Crontab setup
```
#Enter editor for crontab jobs
crontab -e

#Add the following to crontab
#Edit custom_path to the correct path to your directory
* * * * * bash /custom_path/linux_sql/scripts/host_usage.sh localhost 5432 host_agent postgres password > /tmp/host_usage.log
```

# Implemenation
## Architecture
![Architecture diagram](./assets/diagram)

## Scripts
- psql_docker.sh
    - This script will create, start or stop a docker container. A username and password is required to create the container.
  
```
#Script Usage
./psql_docker.sh create|start|stop [db_username] [db_password]
```
- host_info.sh
  - To be run only once upon installation, this script collects and inserts hardware data into the host_info table.

```
#Script Usage
./host_info.sh psql_host psql_port db_name psql_user psql_password
```
- host_usage.sh
  - This script collects and inputs the current host usage into the host_usage table.

```
#Script Usage
./host_usage.sh psql_host psql_port db_name psql_user psql_password
```
- crontab
  - Runs host_usage.sh every minute.

```
#Script Usage
#Edit custom_path to the correct path to your directory
* * * * * bash /custom_path/linux_sql/scripts/host_usage.sh localhost 5432 host_agent postgres password > /tmp/host_usage.log
```

## Database Modeling
Describe the schema of each table using markdown table syntax (do not put any sql code)
- `host_info`

| Table Column | Data Type | Constraints                   | Notes                       |
|---|-----------|-------------------------------|-----------------------------|
| id | SERIAL    | primary key                   |                             |
| hostname | VARCHAR   | unique                        |                             |
| cpu_number | INT       |                               |                             |
|cpu_architecture| VARCHAR   |                               |                             |
|cpu_model| VARCHAR   |                               |                             |
|cpu_mhz| FLOAT     |                               |                             |
|l2_cache| INT       |                               | units: kB                   |
|timestamp| TIMESTAMP |  | format: YYYY-MM-DD HH:MM:SS |
|total_mem| INT       |                               | units: kB                   |


         
- `host_usage`

|Table Column | Data Type | Constraints                        | Notes                 |
|---|-----------|------------------------------------|-----------------------|
|timestamp | TIMESTAMP |                                    | format: YYYY-MM-DD HH:MM:SS |
|host_id| INT       | foreign key references host_id(id) | |
|memory_free| INT      |                                    | units: mB             |
|cpu_idle| INT     |                                    | percentage            |
|cpu_kernel | INT     |                                    | percentage            |
|disk_io | INT       |                                    |                       |
|disk_available | INT      |                                    | units: mB             |


# Test
The bash scripts were tested manually on a single machine. Unfortunately a test was not conducted on a full Linux cluster.

# Deployment
The host_usage.sh script is scheduled using cron and the psql instance provisioned with Docker.

All source code is managed on GitHub

# Improvements
1. Integrate docker start into crontab to start psql container if it is not running.