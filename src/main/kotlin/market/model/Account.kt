package market.model

data class Account(val name: String, var money: Long = 0, val stocks: MutableMap<Stock, Long> = mutableMapOf())
