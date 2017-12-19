package com.github.jarlakxen.healthchecks

package object status {

  final case object Get
  
  sealed trait Liveness
  final case object Alive extends Liveness
  final case class Dead(reason: String, exception: Option[Throwable] = None) extends Liveness

  type LivenessStatus = Either[Dead, Alive.type]

  sealed trait Readiness
  final case object Ready extends Readiness
  final case object Pending extends Readiness

  type ReadinessStatus = Either[Pending.type, Ready.type]
}