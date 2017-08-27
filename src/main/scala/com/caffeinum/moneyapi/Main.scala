package com.caffeinum.moneyapi

import io.circe._
import io.finch._

import io.finch.circe._
import io.circe.generic.auto._

import com.twitter.server.TwitterServer

import com.twitter.finagle.param.Stats
import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http.{Request, Response}

import com.twitter.util.Await
import com.twitter.util.FuturePool


object Main extends TwitterServer {
  val storage = new Storage()

  val ping: Endpoint[String] = get("ping") { Ok("Pong") }

  val expensive: Endpoint[BigInt] = get("expensive" :: int) { i: Int =>
    FuturePool.unboundedPool {
      Ok(  BigInt(i).pow(i)  )
    }
  }

  val test: Endpoint[User] = get("test") {
    val admin = storage.createUser("admin")
    val root = storage.createUser("root")

    Ok(admin)
  }

  val createUser: Endpoint[User] = post("users" :: param("username")) {
    (username: String) => {
      val newUser = storage.createUser(username)
      Ok( newUser )
    }
  }

  val getUser: Endpoint[User] = get("users" :: long) {
    (uid: Long) => Ok( storage.getUser(uid) )
  }

  val getBalance: Endpoint[Long] = get("users" :: long :: "balance") {
    (uid: Long) => Ok( storage.getBalance(uid) )
  }

  val deposit: Endpoint[User] = put("users" :: long :: "deposit" :: param("amount").as[Long]) {
    (uid: Long, amount: Long) => FuturePool.unboundedPool {
      Ok( storage.deposit(uid, amount) )
    }
  }

  val withdraw: Endpoint[User] = put("users" :: long :: "withdraw" :: param("amount").as[Long]) {
    (uid: Long, amount: Long) => FuturePool.unboundedPool {
      Ok( storage.withdraw(uid, amount) )
    }
  }

  // /users/:uid/send/:to ?amount
  val send: Endpoint[User] = put("users" :: long :: "send" :: long :: param("amount").as[Long]) {
    (uid: Long, recipientID: Long, amount: Long) => FuturePool.unboundedPool {
      Ok( storage.send(uid, recipientID, amount) )
    }
  }

  val endpoints = (ping :+: test :+: expensive :+: createUser :+: getUser :+: getBalance :+: deposit :+: withdraw :+: send)

  val enpointsHandled = endpoints.handle {
    case e: NoSuchUserException =>
      log.error("Storage error: No such user ", e)
      NotFound( e )
    case e: StorageException =>
     log.error("Storage error ", e)
     BadRequest( e )
    case e: IllegalArgumentException =>
     log.error("Bad request from client", e)
     BadRequest(e)
    case e: Exception =>
     log.error("Unknown exception", e)
     BadRequest(e)
    case t: Throwable =>
     log.error("Unexpected exception", t)
     InternalServerError(new Exception(t.getCause))
  }

  implicit val encodeExceptionCirce: Encoder[Exception] = Encoder.instance(e =>
    Json.obj("message" -> Option(e.getMessage).fold(Json.Null)(Json.fromString))
  )

  val service: Service[Request, Response] = enpointsHandled.toServiceAs[Application.Json]

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
