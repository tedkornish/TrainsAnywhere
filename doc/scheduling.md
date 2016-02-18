# Scheduling Strategy

This document is an evolving set of thoughts about how to schedule fetching and updating for routes.

Routes in the next week should be updated every 6 hours. Routes in the next month (7-31 days) should be updated once per day. Routes in the next 6 months (31-180 days) should be updated once per week.

The scheduler has a few jobs:

* Every week, upsert routes for the next n months
* Every 10 minutes, if the message queue is small enough, feed in some routes that need fetching or updating. We don't need to fetch past routes.
* Every day, delete routes (and thus trips) whose date is before today.

When inserting trips into the database, we should remember to delete all trip info previously associated with that route so as not to store redundant information (since we don't care about analyzing price over time, only what the most recent price is).

## Which routes need fetching?

In descending order of urgency:

1. Routes which are "waiting" whose date is in the next week and whose last_fetched_at is NULL or more than 6 hours ago. Rank by last_fetched_at DESC, NULL at top (if I can pick), then by origin_station_id ASC.
2. Routes which are "waiting" whose date is in 7-31 days from now and whose last_fetched_at is NULL or more than 1 day ago. Rank by last_fetched_at DESC, NULL at top (if that's an option), then by origin_station_id ASC.
3. Routes which are "waiting" whose date is 31-120 days from now and whose last_fetched_at is NULL or more than 7 days ago. Rank by last_fetched_at DESC, NULL at top (if that's an option), then by origin_station_id ASC.
