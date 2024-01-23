# spring-tiny-link-generator
A Java Spring Boot tiny link generator API, using Firestore as DB, Zookeeper as central synchronizer and Redis as Cache.

* You can clone this repository
* The repository is licensed under the MIT license

## Prerequisites

This application is using some Java 18 functions. Make sure your installed java version is at least 18 and configure your IDE accordingly.

This application requires the following services to run:

* A zookeeper instance available through an endpoint
* A redis instance available through an endpoint
* A mongodb (NoSQL database) instance available through an endpoint

You can run both services (zookeeper & redis) via a Docker manager (like Docker Desktop) by simply pulling and running the corresponding images.
Make sure both services are running, and that their endpoints are configures in the application.properties (or the secrets.properties) file before running the application. The configuration should look like this:

* spring.data.redis.host=localhost
* spring.data.redis.port=6379
* zookeeper.server.url=localhost
* zookeeper.server.port=2181
* spring.data.mongodb.database=UrlPair
* spring.data.mongodb.host=localhost
* spring.data.mongodb.port=27017

The official docker image for zookeeper is available here : https://hub.docker.com/_/zookeeper .

The same goes for redis here : https://hub.docker.com/_/redis . You can also use a provider like https://railway.app/ or the official redis cloud https://app.redislabs.com/ .

The official docker image for mongodb is available here : https://hub.docker.com/_/mongo .

The application is configured to run on the specified port, but to run it in a distributed manner, you just have to change the port to 0 in the application.properties file:

* server.port=0

The application name is already configured to have a random id.

If ran as a cluster with docker compose, it is important to change the configuration of the hosts for the corresponding container names:
* spring.data.redis.host=<redis-container>
* spring.data.redis.port=6379
* zookeeper.server.url=<zookeeper-container>
* zookeeper.server.port=2181
* spring.data.mongodb.database=UrlPair
* spring.data.mongodb.host=<mongodb-container>
* spring.data.mongodb.port=27017

Once the application runs you should see something like this

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.2.1)

2024-01-18T16:27:38.881-05:00  INFO 31004 --- [TinyLinkAPI:60997a0de7673fbaadd657c2d4b7197a] [  restartedMain] ca.dtadmi.tinylink.TinyLinkApplication   : Starting TinyLinkApplication using Java 21.0.1 with PID 31004 (B:\git\spring-tiny-link-generator-api\target\classes started by darke in B:\git\spring-tiny-link-generator-api)
2024-01-18T16:27:38.883-05:00  INFO 31004 --- [TinyLinkAPI:60997a0de7673fbaadd657c2d4b7197a] [  restartedMain] ca.dtadmi.tinylink.TinyLinkApplication   : No active profile set, falling back to 1 default profile: "default"
2024-01-18T16:27:38.922-05:00  INFO 31004 --- [TinyLinkAPI:60997a0de7673fbaadd657c2d4b7197a] [  restartedMain] .e.DevToolsPropertyDefaultsPostProcessor : Devtools property defaults active! Set 'spring.devtools.add-properties' to 'false' to disable
2024-01-18T16:27:38.922-05:00  INFO 31004 --- [TinyLinkAPI:60997a0de7673fbaadd657c2d4b7197a] [  restartedMain] .e.DevToolsPropertyDefaultsPostProcessor : For additional web related logging consider setting the 'logging.level.web' property to 'DEBUG'
2024-01-18T16:27:39.516-05:00  INFO 31004 --- [TinyLinkAPI:60997a0de7673fbaadd657c2d4b7197a] [  restartedMain] .s.d.r.c.RepositoryConfigurationDelegate : Multiple Spring Data modules found, entering strict repository configuration mode
2024-01-18T16:27:39.518-05:00  INFO 31004 --- [TinyLinkAPI:60997a0de7673fbaadd657c2d4b7197a] [  restartedMain] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data Redis repositories in DEFAULT mode.
2024-01-18T16:27:39.541-05:00  INFO 31004 --- [TinyLinkAPI:60997a0de7673fbaadd657c2d4b7197a] [  restartedMain] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 11 ms. Found 0 Redis repository interfaces.
2024-01-18T16:27:40.084-05:00  INFO 31004 --- [TinyLinkAPI:60997a0de7673fbaadd657c2d4b7197a] [  restartedMain] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port 7787 (http)
2024-01-18T16:27:40.093-05:00  INFO 31004 --- [TinyLinkAPI:60997a0de7673fbaadd657c2d4b7197a] [  restartedMain] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2024-01-18T16:27:40.093-05:00  INFO 31004 --- [TinyLinkAPI:60997a0de7673fbaadd657c2d4b7197a] [  restartedMain] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/10.1.17]
2024-01-18T16:27:40.140-05:00  INFO 31004 --- [TinyLinkAPI:60997a0de7673fbaadd657c2d4b7197a] [  restartedMain] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2024-01-18T16:27:40.140-05:00  INFO 31004 --- [TinyLinkAPI:60997a0de7673fbaadd657c2d4b7197a] [  restartedMain] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 1217 ms
2024-01-18T16:27:41.323-05:00  INFO 31004 --- [TinyLinkAPI:60997a0de7673fbaadd657c2d4b7197a] [  restartedMain] o.s.b.d.a.OptionalLiveReloadServer       : LiveReload server is running on port 35729
2024-01-18T16:27:41.350-05:00  INFO 31004 --- [TinyLinkAPI:60997a0de7673fbaadd657c2d4b7197a] [  restartedMain] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 7787 (http) with context path ''
2024-01-18T16:27:41.357-05:00  INFO 31004 --- [TinyLinkAPI:60997a0de7673fbaadd657c2d4b7197a] [  restartedMain] ca.dtadmi.tinylink.TinyLinkApplication   : Started TinyLinkApplication in 2.766 seconds (process running for 3.388)

```

## About the Service

The service is a tiny url converter. It takes long url and converts them into short links. 

The specifications and assumptions taken for this application are as follows:

* Being able to handle billions of entries/conversions
* No concurrent access expected (we designed it nonetheless to be able to scale in that direction)
* The generated URLs, domain excluded, should be 10 characters maximum
* The conversion should be idempotent
* Converted urls have to be long-lived (indefinitely)
* The service should make the conversion itself, instead of rely on an external API
* No authentication required
* The API will be consumed by a frontend to query the conversions
* The user should be able to close the application, restart it and still have access to their previously converted urls


### Architecture

We decided to implement a scalable system that could handle an important load of concurrent queries. For this reason, we decided to have the url max length configurable ( tiny.link.size=10 ), and to implement some rate limiting and load limiting strategies, along with some caching to improve performance when converting. 

It also helps to make the conversion idempotent (always return the same result for the same query), although that is mainly achieved by using a database to store the results. We decided on a DAO + service + controller strategy to handle the CRUD, as the data is not complex enough to warrant a Repository strategy. As a data model, we went with the following :

* an id : for ease of retrieval and manipulation
* a long url
* a short url
* a creation date : to sort the results when querying the most recent ones

Since we decided to implement some backend pagination, we went with a DTO strategy, to prepare the values before sending them to the frontend. This dto contains the data and some metadata, consisting of the following:
* page : starts at 1
* number of entries per page : we went with 3 for a better UI experience
* number of pages : it'll be 4 for our values but would change with a different configuration
* number total of entries : we went with 10, for a list of the 10 most recent conversions, but it's configurable in the application.properties file ( max.number.history.results=10 )


To respect the specification, using a base 62 conversion seems adequate, since a combination of 10 characters from 62 is 62^10 values, enough that even at a rate of 1000 per second, it would take more than 26Ml years to generate all the possible short urls and reach the end. But that's only if the seed we use is always unique, as all number based conversions are idempotent.
Thus, it seems like using the long url as an entry and encode it, to then take the 10 first characters of the converted string is not a good idea, as we would inevitably have collisions. We could also check in the db to see if the value already exist before storing it, and pick another combination of characters if it's not the case, but that doesn't really solve the issue in the long run and is not very efficient.

A better solution would be a counter, as its values would always increment, hence always be different. That being said, this wouldn't work in a distributed environment, as any instance of the server could produce an already produced value.

That issue is solved if the counter is itself distributed, hence the use of a coordinator like zookeeper that can be run on a separate server or a docker image. It would be better to have multiple instance of the coordinator, on different machines, with a leader and a quorum, but we will let that implementation for eventual scaling.

For now, the counter issue is solved by zookeeper, the performance issue by using redis as a cache, and the idempotence by Firestore as a DB.

That leaves the redirection. The best solution is to implement it on the server side, not the frontend, as the redirection would still be working without the client being up, as long as the backend is up. To try and have the shortest possible link, we set the redirection mapping at the root of the path "tinylink.ca/sdfs5f4d", instead of having a longer path like "tinylink.ca/redirect/sdfs5f4d" (where tinylink.ca is a made-up example of url of our server once deployed).

We also set some cors configuration to only allow the frontend application to connect. Make sure the configuration is done on both side, before running the application. It looks like this, in the application.properties file:
* client.base.url=http://localhost:7667/

### Swagger issue

We faced an issue while trying to implement the self documentation tool for REST API swagger, as it kept colliding with our redirection url. Fearing not having enough time for everything we planed, we put it in the "maybe later" list.

### Tests

Run mvn clean test to run the test suite

### Optimization issue

The code could benefit from some optimization, but we decided to focus on solving the problem first, and optimize later if possible. Thus, optimization also went into the "maybe later" list.

### Deployment

We implemented a GitHub action to execute the docker compose that launches:
* the redis instance: the standard redis docker image 
* the zookeeper instance: the standard zookeeper docker image
* the mongodb instance: the standard mongodb docker image
* the api server: the docker image generated by building the Dockerfile present in the sources

To run it locally:
* execute a mvn clean install
* run a build on the Dockerfile (after establishing a connection to Dockerhub)
* run the docker-compose file

These are the steps executed, in that order, by the CI/CD GitHub action.

### Example of requests

* Get most recent conversions : http://localhost:7787/api/tinylink/urlPairs?page=1
  * It returns an object containing a list of url pairs and their id, and the corresponding pagination metadata
* Get short from long url : http://localhost:7787/api/tinylink/urlPairs/shortUrl 
  * body : {
    "longUrl": "https://github.com/31z4/zookeeper-docker/tree/master"
    }
  * It returns a short url as a string
* Get long from short url : http://localhost:7787/api/tinylink/urlPairs/longUrl 
    * body : {
    "shortUrl": "http://localhost:7787/Q"
    }
  * It returns a long url as a string

### Frontend

The corresponding frontend code is available here: https://github.com/DTADMI/react-tiny-link-app.git .


# Questions and Comments: tadmidarryl@gmail.com

