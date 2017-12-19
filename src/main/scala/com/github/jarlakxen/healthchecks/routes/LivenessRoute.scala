package com.github.jarlakxen.healthchecks.routes

import scala.concurrent.duration._

import akka.actor.{ Actor, ActorSystem, Props }
import akka.http.scaladsl.model.{ HttpEntity, StatusCode, StatusCodes }
import akka.http.scaladsl.server.{ Directives, Route }
import akka.pattern.ask
import akka.util.Timeout

import com.github.jarlakxen.healthchecks.status._

object LivenessRoute extends ActorRoute {

  private class Listener(
      aliveBody: Option[BodyGenerator],
      errorBody: Option[ErrorBodyGenerator]) extends Actor {

    def receive = aliveStatus

    def aliveStatus: Receive = {
      case Get =>
        sender() ! ((StatusCodes.OK, aliveBody.map(_())))
      case Dead(reason, exception) =>
        context.become(deadStatus(reason, exception))
    }

    def deadStatus(reason: String, exception: Option[Throwable]): Receive = {
      case Get =>
        sender() ! ((StatusCodes.InternalServerError, errorBody.map(_(reason, exception))))
      case Alive =>
        context.become(aliveStatus)
    }
  }

  def apply(
      pathName: String, 
      aliveBody: Option[BodyGenerator] = None,
      errorBody: Option[ErrorBodyGenerator] = None)(implicit sys: ActorSystem): Route = {
    val listenerRef = sys.actorOf(Props(classOf[Listener], aliveBody, errorBody))
    sys.eventStream.subscribe(listenerRef, Alive.getClass)
    sys.eventStream.subscribe(listenerRef, classOf[Dead])

    buildRoute(pathName, statusResolver(listenerRef))
  }

}