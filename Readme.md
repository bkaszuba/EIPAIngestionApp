#EIPA INGESTION APP

This project calls periodically EIPA dynamic endpoint to fetch data, saves them and then sends only updated data to configured endpoint in config file.

### Configuration
Configuration is required to run this application. It contains 3 sections and all are required to be filled for everything to work correctly
* `eipa.ingestion.app`
  * `polling-rate` - time in ms for periodic call to EIPA for data
* `downstream.server`
  * `endpoint` - endpoint to be called with updated data from EIPA
* `eipa.server`
  * `endpoint` - EIPA endpoint for dynamic data
  * `api-key` - api-key to that resource

### Running application
To run this application please fill configuration file first and then run follwing two commands:
* **mvn package**
* **mvn exec:java**


