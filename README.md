# trainsanywhere

## General architecture

4 main services running:

* Many scraper instances pull requests for days and routes off a queue, scrape them, and insert into the DB
* The scheduler figures out which dates need to be scraped and queues them up
* The JSON API serves information about routes over HTTP, pulling out of the same DB the scrapers are talking to
* The client talks to the JSON API

## Database connection & migration

Before a process needing to talk to the Postgres database is started, the `TA_POSTGRES` environment variable should be set to a Postgres connection string.

For now, since it's just one person working on the project, migrations are identified by incrementing IDs. Later we can switch to something like datetime-identified migrations.

To migrate or rollback: `lein migrate` or `lein rollback`.

## Scraping stations

To populate the database with stations ordered by popularity, call `lein seed-stations`.
