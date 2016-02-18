# Scheduling Strategy

This document is an evolving set of thoughts about how to schedule fetching and updating for routes.

Routes in the next week should be updated every 6 hours. Routes in the next month (7-31 days) should be updated once per day. Routes in the next 6 months (31-180 days) should be updated once per week.

The scheduler has a few jobs:

* Every hour, upsert routes for the next 6 months
* Every 5 minutes, if the message queue is small enough, feed in some routes that need fetching or updating. We don't need to fetch past routes.
* Every hour, delete routes (and thus trips) whose date is before today.

When inserting trips into the database, we should remember to delete all trip info previously associated with that route so as not to store redundant information (since we don't care about analyzing price over time, only what the most recent price is).
