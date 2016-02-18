# TrainsAnywhere

A travel discovery site which scrapes train times and prices from the Rail Europe website. See the `docs` folder for more specific topics.

## Database connection & migration

Before a process needing to talk to the Postgres database is started, the `TA_POSTGRES` environment variable should be set to a Postgres connection string.

For now, since it's just one person working on the project, migrations are identified by incrementing IDs. Later we can switch to something like datetime-identified migrations.

To migrate or rollback: `lein migrate` or `lein rollback`.

## Scraping stations

To populate the database with stations ordered by popularity, call `lein seed-stations`.
