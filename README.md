# akka-http-healthchecks
[![Download](https://api.bintray.com/packages/jarlakxen/maven/akka-http-healthchecks/images/download.svg)](https://bintray.com/jarlakxen/maven/akka-http-healthchecks/_latestVersion)
[![License](https://img.shields.io/badge/license-Apache_2.0-blue.svg)](LICENSE)

Akka HTTP healthcheck library for [Kubernetes liveness/readiness probes][k8sprobes].

## Installation
```scala
resolvers += Resolver.bintrayRepo("jarlakxen","maven")
  
libraryDependencies += "com.github.jarlakxen" %% "akka-http-healthchecks" % <version>
```

## Getting Started

This library use the `ActorSystem` default event bus to notify the application's state changes.

### Setup healthcheck endpoints

```scala
  import akka.actor.ActorSystem
  import akka.stream.ActorMaterializer
  import akka.http.scaladsl.Http

  import com.github.jarlakxen.healthchecks._
  
  implicit val system       = ActorSystem()
  implicit val materializer = ActorMaterializer()


  // start web server listening "0.0.0.0:9000/readiness" and "0.0.0.0:9000/liveness"
  val serverBinding = Http().bindAndHandle(
    HealthCheckRoutes.routes(),
    "0.0.0.0",
    9000
  )
```

### Check endpoints

```scala
  import akka.actor.ActorSystem
  import akka.stream.ActorMaterializer
  import akka.http.scaladsl.Http

  import com.github.jarlakxen.healthchecks._
  
  implicit val system       = ActorSystem()
  implicit val materializer = ActorMaterializer()


  // status code is 200(OK) if healthy, 503(Service Unavailable) if unhealthy.
  val response1 = Http().singleRequest(HttpRequest(uri = "http://localhost:8888/readiness"))

  // status code is 200(OK) if healthy, 500(Internal Server Error) if unhealthy.
  val response2 = Http().singleRequest(HttpRequest(uri = "http://localhost:8888/liveness"))
```

### Change state

```scala
  import akka.actor.ActorSystem

  import com.github.jarlakxen.healthchecks._
  
  implicit val system       = ActorSystem()

  // The '/readiness' return 503 by default. To change the state of the
  // application to ready ( /readiness returns 200 ) use:
  HealthCheck.ready
  
  // The '/liveness' return 200 by default. To change the state of the
  // application to not alive ( /liveness returns 500 ) use:
  HealthCheck.error("Something fail", Some(ex))
```

### Configure Kubernetes

Then you can set kubernetes liveness/readiness probe in the kubernetes manifest like below:

```yaml
...
  livenessProbe:
    httpGet:
      path: /liveness
      port: 9000
      initialDelaySeconds: 3
      periodSeconds: 3
  readinessProbe:
    httpGet:
      path: /readiness
      port: 9000
      initialDelaySeconds: 3
      periodSeconds: 3
...
```

## Contribution policy ##

Contributions via GitHub pull requests are gladly accepted from their original author. Along with any pull requests, please state that the contribution is your original work and that you license the work to the project under the project's open source license. Whether or not you state this explicitly, by submitting any copyrighted material via pull request, email, or other means you agree to license the material under the project's open source license and warrant that you have the legal authority to do so.


## License
This code is open source software licensed under Apache Version 2.0 License.

[k8sprobes]: https://kubernetes.io/docs/tasks/configure-pod-container/configure-liveness-readiness-probes/ "Kubernetes liveness/readiness probe"