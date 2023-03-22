package market

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import market.model.Account
import market.model.Stock
import java.lang.IllegalArgumentException

class Market {
    private val stocks: MutableMap<Stock, Long> = mutableMapOf()
    private val accounts: MutableMap<Int, Account> = mutableMapOf()

    fun start() {
        embeddedServer(Netty, 8080) {
            routing {
                get("/createStock") {
                    val parameters = this.context.request.queryParameters
                    try {
                        val newStock = Stock(parameters["name"]!!, parameters["price"]!!.toLong())
                        if (stocks.filter { it.key.name == newStock.name }.isNotEmpty()) {
                            throw IllegalArgumentException("There is such stock already")
                        }
                        stocks[newStock] = parameters["amount"]!!.toLong()
                        call.respondText { "Success" }
                    } catch (e: Exception) {
                        call.respondText { e.localizedMessage }
                    }
                }
                get("/addStock") {
                    val parameters = this.context.request.queryParameters
                    try {
                        val stock = stocks.filter { it.key.name == parameters["name"]!! }.toList()[0]
                        stocks[stock.first] = stock.second + parameters["amount"]!!.toLong()
                        call.respondText { "Success" }
                    } catch (e: Exception) {
                        call.respondText { e.localizedMessage }
                    }
                }
                get("/getStock") {
                    val parameters = this.context.request.queryParameters
                    try {
                        val stock = stocks.filter { it.key.name == parameters["name"]!! }.toList()[0]
                        call.respondText { "${stock.first}: ${stock.second}" }
                    } catch (e: Exception) {
                        call.respondText { e.localizedMessage }
                    }
                }
                get("/createAccount") {
                    val parameters = this.context.request.queryParameters
                    try {
                        val oldAccount: Account? = accounts[parameters["id"]!!.toInt()]
                        if (oldAccount != null) {
                            throw IllegalArgumentException("There is such account already")
                        }
                        accounts[parameters["id"]!!.toInt()] = Account(parameters["name"]!!, 0)
                        call.respondText { "Success" }
                    } catch (e: Exception) {
                        call.respondText { e.localizedMessage }
                    }
                }
                get("/addMoney") {
                    val parameters = this.context.request.queryParameters
                    try {
                        val account: Account = accounts[parameters["id"]!!.toInt()]
                            ?: throw IllegalArgumentException("There is no such account")
                        account.money += parameters["amount"]!!.toLong()
                        call.respondText { "Success" }
                    } catch (e: Exception) {
                        call.respondText { e.localizedMessage }
                    }
                }
                get("/getAccount") {
                    val parameters = this.context.request.queryParameters
                    try {
                        val account: Account = accounts[parameters["id"]!!.toInt()]
                            ?: throw IllegalArgumentException("No such account")
                        call.respondText { account.toString() }
                    } catch (e: Exception) {
                        call.respondText { e.localizedMessage }
                    }
                }
                get("/buyStock") {
                    val parameters = this.context.request.queryParameters
                    try {
                        val account: Account = accounts[parameters["id"]!!.toInt()]
                            ?: throw IllegalArgumentException("There is no such account")
                        val stock = stocks.filter { it.key.name == parameters["name"]!! }.toList()[0]
                        val price = stock.first.price
                        val amountToBuy = parameters["amount"]!!.toLong()
                        if (stock.second < amountToBuy)
                            throw IllegalArgumentException("not enough stocks")
                        if (price * amountToBuy > account.money)
                            throw IllegalArgumentException("not enough money")
                        account.money -= price * amountToBuy
                        stocks[stock.first] = stocks[stock.first]!! - amountToBuy
                        account.stocks[stock.first] = (account.stocks[stock.first] ?: 0) + amountToBuy
                        call.respondText { "Success" }
                    } catch (e: Exception) {
                        call.respondText { e.localizedMessage }
                    }
                }
                get("/sellStock") {
                    val parameters = this.context.request.queryParameters
                    try {
                        val account: Account = accounts[parameters["id"]!!.toInt()]
                            ?: throw IllegalArgumentException("There is no such account")
                        val stock = account.stocks.filter { it.key.name == parameters["name"]!! }.toList()[0]
                        val price = stock.first.price
                        val amountToSell = parameters["amount"]!!.toLong()
                        if (stock.second < amountToSell)
                            throw IllegalArgumentException("not enough stocks")

                        account.stocks[stock.first] = account.stocks[stock.first]!! - amountToSell
                        account.money += amountToSell * price
                        stocks[stock.first] = stocks[stock.first]!! + amountToSell
                        call.respondText { "Success" }
                    } catch (e: Exception) {
                        call.respondText { e.localizedMessage }
                    }
                }
                get("/changePrice") {
                    val parameters = this.context.request.queryParameters
                    try {
                        val stock = stocks.filter { it.key.name == parameters["name"]!! }.toList()[0]
                        stock.first.price = parameters["price"]!!.toLong()
                    } catch (e: Exception) {
                        call.respondText { e.localizedMessage }
                    }
                }
            }
        }.start(true)
    }
}