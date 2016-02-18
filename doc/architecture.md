# TrainsAnywhere Architecture

## Services

TrainsAnywhere runs 5 types of Clojure services, plus some datastores.

* Many **scraper** instances (likely 50+ in prod) pull requests for routes off of a Redis message queue (`to-scrape`), visit and parse information from the RailEurope website, and queues the trip information to the persistence engine.
* One **persistence** engine pulls scraped HTTP data off the `to-write` queue, formats it in a relational manner, and writes it to Postgres. We can't write directly from scrapers because they would exhaust all of our database connections.
* The **scheduler** figures out which routes need scraping, either because they're new or are out of date, and queues requests for those routes into Redis at regular intervals for the scrapers to pick up.
* The JSON **API** services information about routes which have been pulled over HTTP.
* The **client** is a Clojurescript app which talks from the browser to the API.

## Models

* A **station** is a location in Europe with a lat/long, a city and a country. We scrape a one-off list of these from the Rail Europe API, having found an unauthenticated endpoint which gives us stations from most to least popular.
* A **route** is a triple of `(starting station, ending station, day)`. We generate routes which need fetching every hour or so through a quartzite job in the scheduler. Given some limit of stations to fetch (let's say 120) we compute all origin-destination pairs of stations and fetch those with varying frequencies.
* A **trip** is one instance of an end-to-end train trip on a route. A route has many trips because there are many train trips between the same origin and terminus per day.
* A **hop** is a subunit of a trip, representing the idea that a trip might be composed of several shorter train rides with stops in various cities.
