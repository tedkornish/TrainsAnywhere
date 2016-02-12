# trainsanywhere

## General architecture

4 main services running:

* Many scraper instances pull requests for days and routes off a queue, scrape them, and insert into the DB
* The scheduler figures out which dates need to be scraped and queues them up
* The JSON API serves information about routes over HTTP, pulling out of the same DB the scrapers are talking to
* The client talks to the JSON API
