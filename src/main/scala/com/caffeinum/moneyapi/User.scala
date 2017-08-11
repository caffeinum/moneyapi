package com.caffeinum.moneyapi

import java.util.UUID

case class User(val id: UUID, val username: String) {
  def balance = 100
}

case class MoneyTransfer(val id: UUID, val amount: Int, val sender: UUID, val recipient: UUID)
