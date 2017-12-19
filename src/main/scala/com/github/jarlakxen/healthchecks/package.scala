package com.github.jarlakxen

import akka.actor.ActorSystem
import akka.http.scaladsl.server.{ Directives, Route }

import healthchecks.body._
import healthchecks.routes._
import healthchecks.status._

package object healthchecks {

  import Directives._

  object HealthCheckRoutes {

    val defaultLivenessPathName = "liveness"
    val defaultReadinessPathName = "readiness"

    def liveness(
      pathName: String = defaultLivenessPathName,
      aliveBody: Option[BodyGenerator] = None,
      errorBody: Option[ErrorBodyGenerator] = Some(errorBodyGeneratorError))(implicit sys: ActorSystem): Route =
      LivenessRoute(pathName, aliveBody, errorBody)

    def readiness(
      pathName: String = defaultReadinessPathName,
      pendingBody: Option[BodyGenerator] = None,
      readyBody: Option[BodyGenerator] = None)(implicit sys: ActorSystem): Route =
      ReadinessRoute(pathName, pendingBody, readyBody)

    def routes(
      livenessPathName: String = defaultLivenessPathName,
      readinessPathName: String = defaultReadinessPathName)(implicit sys: ActorSystem): Route =
      liveness(livenessPathName) ~ readiness(readinessPathName)

  }

  object HealthCheck {

    def ready(implicit sys: ActorSystem): Unit =
      sys.eventStream.publish(Ready)

    def alive(implicit sys: ActorSystem): Unit =
      sys.eventStream.publish(Alive)

    def error(reason: String, exception: Option[Throwable] = None)(implicit sys: ActorSystem): Unit =
      sys.eventStream.publish(Dead(reason, exception))

  }

}