package com.caffeinum.moneyapi.persistence

import java.util.concurrent.atomic.AtomicLong

import com.caffeinum.moneyapi.{User, MoneyTransfer}

import scala.collection.concurrent.TrieMap

class InMemoryRepository extends MoneyRepository {

  private val users = TrieMap[Long, User]()
  private val transfers = TrieMap[Long, MoneyTransfer]()
  private val idSequence = new AtomicLong()

  def getUsers: Seq[User] = users.values.toSeq
  def getUserById(id: Int): Option[User] = ???
  def create(user: User): User = ???
  def delete(id: Int): Unit = ???
  //def update(id: Int, userForm: UserForm): Option[User]

  def create(transfer: MoneyTransfer): MoneyTransfer = ???
  //def update(id: Int, transfer:): MoneyTransfer
  def getTransferById(id: Int): MoneyTransfer = ???
  def getTransfersByUserId(id: Int): Seq[MoneyTransfer] = ???
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
}
