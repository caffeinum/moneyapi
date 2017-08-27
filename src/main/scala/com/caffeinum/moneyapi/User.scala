package com.caffeinum.moneyapi

import java.util.UUID

case class User(val id: Long, val username: String, val balance: Long = 0)

case class MoneyTransfer(val senderID: Long, val recipientID: Long, val amount: Long)
