package com.caffeinum.moneyapi

import org.scalatest._
import com.caffeinum.moneyapi._
import scala.util.Random

class StorageSpec extends FlatSpec with Matchers {
  val storage = new Storage()

  val admin = storage.createUser("admin")
  val root  = storage.createUser("root")

  "Storage" should "save user correctly" in {
    val rID = root.id

    val rootGET = storage.getUser(root.id)
    rootGET.username shouldEqual "root"
  }

  it should "give different id to different users" in {
    admin.id should not be root.id
  }

  it should "give different id to same names" in {
    val test1 = storage.createUser("test_user")
    val test2 = storage.createUser("test_user")

    test1.id should not be test2.id
  }

  it should "deposit user" in {
    storage.deposit(admin.id, 100)

    val balanceAdmin = storage.getBalance(admin.id)

    balanceAdmin shouldEqual 100
  }

  it should "be able to send money" in {
    storage.send(admin.id, root.id, 50)

    val balanceRoot   = storage.getBalance(root.id)
    val balanceAdmin  = storage.getBalance(admin.id)

    balanceRoot shouldEqual 50
    balanceAdmin shouldEqual 50
  }

  it should "not let send more than you have" in {
    val oldBalance = storage.getBalance(admin.id)
    intercept[NotEnoughFundsException] {
     storage.send(admin.id, root.id, 100)
    }
    val newBalance = storage.getBalance(admin.id)

    newBalance shouldEqual oldBalance
  }

  it should "not let withdraw more than you have" in {
    val oldBalance = storage.getBalance(admin.id)
    intercept[NotEnoughFundsException] {
     storage.withdraw(admin.id, 9000)
    }
    val newBalance = storage.getBalance(admin.id)

    newBalance shouldEqual oldBalance
  }

  it should "deal with concurrent operations" in {

    val balanceRoot   = storage.getBalance(root.id)
    val balanceAdmin  = storage.getBalance(admin.id)
    val gen = new Random()

    for (i <- 1 to 10000) {
      val thread = new Thread {
        override def run {
          try {
            val amount = gen.nextInt(100) - 100/2
            if (amount > 0)
              storage.send(admin.id, root.id, amount)
            else
              storage.send(root.id, admin.id, -amount)
          } catch { case e: NotEnoughFundsException => }
        }
      }
      thread.start
    }

    Thread.sleep(1000)

    val sumAfter = storage.getBalance(root.id) + storage.getBalance(admin.id)

    (balanceRoot + balanceAdmin) shouldEqual sumAfter

  }

}
