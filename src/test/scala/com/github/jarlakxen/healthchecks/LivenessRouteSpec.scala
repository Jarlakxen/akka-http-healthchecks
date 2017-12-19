package com.github.jarlakxen.healthchecks

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit._

import status._

class LivenessRouteSpec extends Spec with ScalatestRouteTest {

  override def afterAll {
    cleanUp()
  }

  val routes = HealthCheckRoutes.routes()

  "LivenessRoute" should "return 200 by default" in {
    Get(s"/liveness") ~> routes ~> check {
      status shouldEqual StatusCodes.OK
    }
  }

  it should "change to InternalServerError if something is wrong" in {
    HealthCheck.error("test")
    Get(s"/liveness") ~> routes ~> check {
      status shouldEqual StatusCodes.InternalServerError
      entityAs[String] shouldEqual """|{
      |  "reason": "test"
      |  
      |}""".stripMargin
    }
  }

  it should "go back to alive" in {
    HealthCheck.error("test")
    HealthCheck.alive
    Get(s"/liveness") ~> routes ~> check {
      status shouldEqual StatusCodes.OK
    }
  }

}