# Configuration

This file mostly contains information about which environment variables get used where. Since we aim to be as much of a [12-factor app](http://12factor.net/) as possible, we read all our environment-specific configuration in from environment variables.

## Database config

At start-time, each Clojure process which needs to talk to Postgres will read Postgres connection information from the following environment variables:

* `TA_PSQL_USER`
* `TA_PSQL_PASSWORD`
* `TA_PSQL_PORT`
* `TA_PSQL_HOST`
* `TA_PSQL_DB`

## Scheduler config

The scheduler figures out how many stations to use in its pairwise route creation by looking at the `TA_NUM_STATIONS` variable, which should be an integer equal to or greater than 2. Default `120`.
