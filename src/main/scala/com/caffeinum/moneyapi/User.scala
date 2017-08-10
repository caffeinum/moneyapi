package com.caffeinum.moneyapi

case class User(val id: Int, val username: String)

case class Transfer(val id: Int, val amount: Int, val sender: User, val recipient: User)
