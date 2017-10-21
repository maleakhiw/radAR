# Tests
Unit and integration tests for the backend.

To run, just run `npm test`. Make sure you already have all the dependencies installed by running `npm install`.

The instrumentation tests (those without `unit` in the file name) depend on an active MongoDB server and will be using the `radarTest` collection as the data store. In production (as set in `.env`), the collection `radar` will be used instead.
