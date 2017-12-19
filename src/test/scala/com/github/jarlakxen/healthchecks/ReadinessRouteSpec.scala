
package com.github.jarlakxen.healthchecks

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit._

import status._

class ReadinessRouteSpec extends Spec with ScalatestRouteTest {

  override def afterAll {
    cleanUp()
  }

  val routes = HealthCheckRoutes.routes()

  "ReadinessRoute" should "return 503 by default" in {
    Get(s"/readiness") ~> routes ~> check {
      status shouldEqual StatusCodes.ServiceUnavailable
    }
  }

  it should "return 200 after app is ready" in {
    HealthCheck.ready
    Get(s"/readiness") ~> routes ~> check {
      status shouldEqual StatusCodes.OK
    }
  }

  it should "after app is ready it cannot change it's state" in {
    HealthCheck.ready
    system.eventStream.publish(Pending)
    Get(s"/readiness") ~> routes ~> check {
      status shouldEqual StatusCodes.OK
    }
  }

}