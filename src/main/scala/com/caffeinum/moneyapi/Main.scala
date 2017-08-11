package com.caffeinum.moneyapi

import java.util.UUID

import io.circe._
import io.finch._

//import io.circe.generic.auto._
import io.finch.circe._

import com.twitter.finagle.param.Stats
import com.twitter.server.TwitterServer
import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Await

object Main extends TwitterServer {

  val ping: Endpoint[String] = get("ping") { Ok("Pong") }

  /*
  // GRUD for /user/:id
  val user: Endpoint[User] = get( "user" :: uuid ) {
    (id: UUID) => Ok( User(id, "caffeinum") )
  }


  // GET /user/:id/balance
  val balance: Endpoint[Int] = get( "user" / uuid / "balance" ) {
    id => Ok( User(id, "caffeinum").balance )
  }

  // PUT /user/:id/send
  val sender: RequestReader[Int] = (param("sender")).as[Int]
  val send: Endpoint[MoneyTransfer] = put( "user" / uuid / "send" / string ? sender ) {
    (id: UUID, amount: Int, sender: UUID) =>
    Ok( MoneyTransfer(new UUID(0L, 0L), amount, sender, id) )
  }

  // POST /tranfer/?from&to&amount
  case class MoneyTransferForm(val sender: UUID, val recipient: UUID, val amount: Int)
  val transferInfo = (param("sender") :: param("recipient") :: param("amount")).as[MoneyTransferForm]
  val transfer: Endpoint[MoneyTransfer] = post( "transfer" ? transferInfo ) {
    (transfer: MoneyTransferForm) =>
    Ok( MoneyTransfer(
      new UUID(0L, 0L), transfer.amount, transfer.sender, transfer.recipient) )
  }

*/
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
