package com.caffeinum.moneyapi

import io.finch._

import com.twitter.finagle.param.Stats
import com.twitter.server.TwitterServer
import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Await

object Main extends TwitterServer {

  val ping: Endpoint[String] = get("ping") { Ok("Pong") }
  val service: Service[Request, Response] = ping.toServiceAs[Text.Plain]

  def main(): Unit = {
    val server = Http.server
      .withAdmissionControl
      .concurrencyLimit(
       maxConcurrentRequests = 10,
       maxWaiters = 10)
      .serve(":8080", service)

    onExit { server.close() }

    Await.ready(adminHttpServer)
  }
}
