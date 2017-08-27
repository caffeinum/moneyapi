package com.caffeinum.moneyapi

import java.util.concurrent.atomic.AtomicLong
import scala.collection.concurrent.TrieMap
import io.circe._

class StorageException(message: String) extends Exception(message) {
  def toJson = Json.obj("error" -> Json.fromString(message))
  override def toString = message
}

case class NotEnoughFundsException(message: String) extends StorageException(message)
case class NoSuchUserException(message: String) extends StorageException(message)
case class WrongAmountException(message: String) extends StorageException(message)
case class WrongSumException(message: String) extends StorageException(message)
case class UserExistsException(message: String) extends StorageException(message)


class Storage {
  private var users = TrieMap[Long, User]()
  private var transfers = TrieMap[Long, MoneyTransfer]()
  //private var backlog = ???
  private var idSequence = new AtomicLong()

  def getUserById(id: Long): Option[User] = users.get(id)
  def getTransferById(id: Long): Option[MoneyTransfer] = transfers.get(id)

  def createUser(username: String): User = {
    val id   = idSequence.getAndIncrement
    val user = User(id, username)

    users.put(id, user)
    user
  }

  def getUser(uid: Long): User = {
    val user = users.get(uid)
    user getOrElse (throw new NoSuchUserException("No user for id " + uid))
  }

  def deposit(uid: Long, amount: Long): User =
    this.synchronized { changeBalance(uid, +amount) }

  def withdraw(uid: Long, amount: Long): User =
    this.synchronized { changeBalance(uid, -amount) }

  def send(senderID: Long, recipientID: Long, amount: Long): User = {
    // bad for speed
    // but when turned off, there are problems when there are more than 100 simultaneous transactions
    this.synchronized {

      val sender    = getUser(senderID)
      val recipient = getUser(recipientID)

      val updatedSender     = sender.copy(    balance = sender.balance    - amount)
      val updatedRecipient  = recipient.copy( balance = recipient.balance + amount)

      val sum = (sender.balance + recipient.balance) - (updatedSender.balance + updatedRecipient.balance)

      if (sum != 0) throw new WrongSumException("Sum before and after transaction is not zero: " + sum)
      else if (amount < 0) throw new WrongAmountException("Cannot send negative amount: " + amount)
      else if (updatedSender.balance < 0) throw new NotEnoughFundsException(sender + " does not have enough funds")
      else {
        users.put(senderID,     updatedSender)
        users.put(recipientID,  updatedRecipient)
        updatedSender
      }
    }
  }

  def changeBalance(uid: Long, amount: Long): User = {
    val user = getUser(uid)
    val updatedUser = user.copy(balance = user.balance + amount)

    if (updatedUser.balance < 0)
      throw new NotEnoughFundsException("User " + user + " does not have enough funds")
    else {
      users.put(uid, updatedUser)
      updatedUser
    }
  }

  def getBalance(uid: Long) = {
    val user = getUser(uid)
    user.balance
  }
}





//
//
//
// class InMemoryRepository extends MoneyRepository {
//
//   private val users = TrieMap[Long, User]()
//   private val idSequence = new AtomicLong()
//
//   def getUsers: Seq[User] = users.values.toSeq
//   def getUserById(id: Int): Option[User] = ???
//   def create(user: User): User = ???
//   def delete(id: Int): Unit = ???
//   //def update(id: Int, userForm: UserForm): Option[User]
//
//   def create(transfer: MoneyTransfer): MoneyTransfer = ???
//   //def update(id: Int, transfer:): MoneyTransfer
//   def getTransferById(id: Int): MoneyTransfer = ???
//   def getTransfersByUserId(id: Int): Seq[MoneyTransfer] = ???
  //

  //
  // override def getAll: Seq[TodoItem] =
  //   repository.values.toSeq
  //
  // override def getById(id: Long): Option[TodoItem] =
  //   repository.get(id)
  //
  // override def create(todoItemForm: TodoItemPostForm): TodoItem = {
  //   val id       = idSequence.getAndIncrement
  //   val todoItem = TodoItem(id, todoItemForm)
  //   repository.put(id, todoItem)
  //   todoItem
  // }
  //
  // override def delete(id: Long): Unit =
  //   repository.remove(id)
  //
  // override def deleteAll: Unit =
  //   synchronized {
  //     idSequence.set(0)
  //     repository.clear()
  //   }
  //
  // override def update(id: Long, todoItemForm: TodoItemPatchForm): Option[TodoItem] = {
  //   val item = repository.get(id).map(_.update(todoItemForm))
  //   item.foreach(repository.update(id, _))
  //   item
  // }
