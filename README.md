# AFAS-Filedownloader
This little project was inspired by my wife. Whom else had to download 7000 files by hand.

## How the application works
When running the application, requests will be made to the AFAS REST API.
The first request that will be made will be to a custom connector that has to be made. 
The connector itself should return data in format:
```
[
    {
        "BijlageID": "File-1",
        "Bijlagenaam": "My File.png",
        "Dossieritem": 1 // Some integer
    },
    {
        "BijlageID": "File-2",
        "Bijlagenaam": "My other file.PDF",
        "Dossieritem": 2 // Some integer
    } .... etc
]
``` 

After the response is received the files will be A-synchronously batched for work.
There will however be a wait on the last item in order for everything to finish in an orderly fashion. 

All work is encapsulated so that might an error occur not ALL work fails.

### Limitations
* Batches are done in 100, as that is the limit of the AFAS REST API.
* A throttle of 1 second per file is implemented in order to not trigger some sort of a DoS protection on the AFAS side.
* A time-out of 5 minutes is maintained in order for connections to not live forever.
* When configuring the work, no check is done if the nrOfItems do not exeed the number of items in AFAS. This is at your own responsibility!
* Note that startingPoint is zero (0) based

### Config file example
```
{
  "abboId" : "12345", // The abboId of the callee
  "connectorName" : "myConnector", // The name of the custom connector
  "tokenBase64" : "some-base64-string", // The token which can be found in AFAS
  "outputDirectory" : "/tmp", // The location the files should be written to
  "nrOfItems" : 100, // The total nr of items you want to download
  "itemsToTake" : 20, // The nr of items to take per batch
  "startingPoint" : 10 // Skip the first N number of items
}
```

## Technical stuff 
### Requirements
* Java 8+
* Maven 3+ (or just use the mvnw)

### Building
A ```mvn clean package``` should be enough to build the project

### Running the project
After building the project run it with ```java jar AFAS-Filedownloader.jar [path-to-config.json]```
 