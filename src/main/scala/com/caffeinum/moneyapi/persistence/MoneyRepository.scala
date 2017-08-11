package com.caffeinum.moneyapi.persistence

import com.caffeinum.moneyapi.{User, MoneyTransfer}

trait MoneyRepository {
  def getUsers: Seq[User]
  def getUserById(id: Int): Option[User]
  def create(user: User): User
  def delete(id: Int): Unit
  //def update(id: Int, userForm: UserForm): Option[User]

  def create(transfer: MoneyTransfer): MoneyTransfer
  //def update(id: Int, transfer:): MoneyTransfer
  def getTransferById(id: Int): MoneyTransfer
  def getTransfersByUserId(id: Int): Seq[MoneyTransfer]

  //
  // def getAll: Seq[TodoItem]
  // def getById(id: Long): Option[TodoItem]
  // def create(todoItemForm: TodoItemPostForm): TodoItem
  // def deleteAll: Unit
  // def delete(id: Long): Unit
  // def update(id: Long, todoItemForm: TodoItemPatchForm): Option[TodoItem]
}
