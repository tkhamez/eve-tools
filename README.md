# eve-tools

https://simple-eve-tools.herokuapp.com

## Setup

### Database

The app needs a PostgreSQL database (tested with version 10).

Create the following environment variables:
```
EVE_TOOLS_JDBC_DATABASE_URL      = jdbc:postgresql://localhost/database
EVE_TOOLS_JDBC_DATABASE_USERNAME = username
EVE_TOOLS_JDBC_DATABASE_PASSWORD = password
```

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
export UID && docker-compose up
export UID && docker-compose run --service-ports eve_tools_java /bin/bash

# second console (find name with "docker ps")
docker exec -it eve-tools_eve_tools_java_run_7869ee4a4b78 /bin/bash
```

## Update

2022-09-17
- Drop tables `spring_session` and `spring_session_attributes` and recreate them by executing `schema-postgresql.sql`.


## Copyright Notice

This project is licensed under the [MIT license](LICENSE).

"EVE", "EVE Online", "CCP" and all related logos and images are trademarks or registered trademarks of 
[CCP hf](https://www.ccpgames.com/). 
