package com.github.jarlakxen.healthchecks

import scala.concurrent._
import scala.concurrent.duration._
import scala.util._

import akka.actor.{ ActorRef, ActorSystem }
import akka.http.scaladsl.model.{ HttpEntity, HttpResponse, StatusCode, StatusCodes }
import akka.http.scaladsl.server.{ Directives, Route }
import akka.pattern.ask
import akka.util.Timeout

import status._

package object routes {
  import Directives._

  type BodyGenerator = () => HttpEntity.Strict
  type ErrorBodyGenerator = (String, Option[Throwable]) => HttpEntity.Strict
  type StatusResolver = () => Future[(StatusCode, Option[HttpEntity.Strict])]

  trait ActorRoute {

    def statusResolver(listenerRef: ActorRef): StatusResolver = {
      implicit val timeout = Timeout(1 seconds)
      () => (listenerRef ? Get).mapTo[(StatusCode, Option[HttpEntity.Strict])]
    }

    def buildRoute(
      pathName: String,
      statusResolver: StatusResolver)(implicit sys: ActorSystem): Route =
      path(pathName) {
        get {
          onComplete(statusResolver()) {
            case Success((statusCode, body)) =>
              complete(HttpResponse(statusCode, entity = body.getOrElse(HttpEntity.Empty)))
            case Failure(ex) =>
              complete(StatusCodes.InternalServerError)
          }
        }
      }
  }

}