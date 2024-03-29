# EVE Tools

Shows you your corporation courier contracts, assets, planetary installations and moon extractions.

https://eve-tools.tian-space.net/

## Setup

### Database

The app needs a PostgreSQL or MySQL database (tested with PostgreSQL 10, 14 and MySQL 8).

Create the following environment variables:
```
EVE_TOOLS_JDBC_DRIVER_NAME       = org.postgresql.Driver ; or com.mysql.cj.jdbc.Driver
EVE_TOOLS_JDBC_DATABASE_URL      = jdbc:postgresql://localhost/database ; or jdbc:mysql://localhost/database
EVE_TOOLS_JDBC_DATABASE_USERNAME = username
EVE_TOOLS_JDBC_DATABASE_PASSWORD = password
```

Create the tables from `schema-postgresql.sql` or `schema-mysql.sql`.

### EVE App

Create an app at https://developers.eveonline.com with the following scopes:
- esi-assets.read_assets.v1
- esi-contracts.read_corporation_contracts.v1
- esi-industry.read_corporation_mining.v1
- esi-planets.manage_planets.v1
- esi-universe.read_structures.v1

Create the following environment variables:
```
EVE_TOOLS_CLIENT_ID    = 12ab
EVE_TOOLS_SECRET_KEY   = ab12
EVE_TOOLS_CALLBACK_URL = http://localhost:8080/sso-callback
```

optionally:
```
EVE_TOOLS_SISI_CLIENT_ID  = 34cd
EVE_TOOLS_SISI_SECRET_KEY = cd34
```

## Run

Tested with Java 11 and 17.

### Dev

```
./gradlew bootRun
```

Browse to http://localhost:8080

Rebuild continuously, in a 2nd console:  
```
./gradlew build --continuous -xtest
```

### Docker

```shell
docker-compose up
docker-compose run --service-ports eve_tools_java /bin/bash

# second console (find name with "docker ps")
docker exec -it eve-tools_eve_tools_java_run_7869ee4a4b78 /bin/bash
```

### Prod

```shell
./gradlew clean bootJar
java -jar build/libs/eve-tools-0.1.0-SNAPSHOT.jar --server.port=8080
```

## Update

2022-09-25
- Execute: `ALTER TABLE eve_user ALTER COLUMN access_token TYPE text;`

2022-09-17
- Drop tables `spring_session` and `spring_session_attributes` and recreate them by executing `schema-postgresql.sql`.

## Copyright Notice

This project is licensed under the [MIT license](LICENSE).

"EVE", "EVE Online", "CCP" and all related logos and images are trademarks or registered trademarks of 
[CCP hf](https://www.ccpgames.com/). 
