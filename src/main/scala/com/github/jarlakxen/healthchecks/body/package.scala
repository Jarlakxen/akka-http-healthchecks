package com.github.jarlakxen.healthchecks

import akka.http.scaladsl.model._

package object body {

  def errorBodyGeneratorError(reason: String, exception: Option[Throwable]): HttpEntity.Strict = {
    val exceptionBody = exception.map(ex => 
      s"""|{
          |  "class": "${ex.getClass.getSimpleName}",
          |  "message": "${ex.getMessage}"
          |}""".stripMargin
    )
    
    HttpEntity(
    s"""|{
        |  "reason": "$reason"${exceptionBody.map(_ => ",").getOrElse("")}
        |  ${exceptionBody.map(node => s"""exception: $node""").getOrElse("")}
        |}""".stripMargin)
  }

}