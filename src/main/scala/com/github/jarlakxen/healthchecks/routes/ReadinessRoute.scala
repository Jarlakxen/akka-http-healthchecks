package com.github.jarlakxen.healthchecks.routes

import scala.concurrent.duration._

import akka.actor.{ Actor, ActorRef, ActorSystem, Props }
import akka.http.scaladsl.model.{ HttpEntity, StatusCode, StatusCodes }
import akka.http.scaladsl.server.{ Directives, Route }
import akka.pattern.ask
import akka.util.Timeout

import com.github.jarlakxen.healthchecks.status._

object ReadinessRoute extends ActorRoute {

  private class Listener(
      pendingBody: Option[BodyGenerator],
      readyBody: Option[BodyGenerator]) extends Actor {

    def receive = pendingStatus

    def pendingStatus: Receive = {
      case Get =>
        sender() ! ((StatusCodes.ServiceUnavailable, pendingBody.map(_())))
      case Ready =>
        context.become(readyStatus)
    }

    def readyStatus: Receive = {
      case Get =>
        sender() ! ((StatusCodes.OK, readyBody.map(_())))
    }
  }

  def apply(
    pathName: String,
    pendingBody: Option[BodyGenerator] = None,
    readyBody: Option[BodyGenerator] = None)(implicit sys: ActorSystem): Route = {
    val listenerRef = sys.actorOf(Props(classOf[Listener], pendingBody, readyBody))
    sys.eventStream.subscribe(listenerRef, Ready.getClass)

    buildRoute(pathName, statusResolver(listenerRef))
  }

}