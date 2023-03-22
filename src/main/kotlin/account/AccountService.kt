package account

class AccountService(val host: String, val port: Int) {
    fun register(id: Int, name: String): String {
        return request("/createAccount", Pair("id", id.toString()), Pair("name", "name"))
    }

    fun addMoney(id: Int, amount: Long): String {
        return request("/addMoney", Pair("id", id.toString()), Pair("amount", amount.toString()))
    }

    fun buyStock(id: Int, name: String, amount: Long): String {
        return request(
            "/buyStock",
            Pair("id", id.toString()),
            Pair("name", name),
            Pair("amount", amount.toString())
        )
    }

    fun sellStock(id: Int, name: String, amount: Long): String {
        return request(
            "/sellStock",
            Pair("id", id.toString()),
            Pair("name", name),
            Pair("amount", amount.toString())
        )
    }

    fun request(address: String, vararg args: Pair<String, String>): String {
        val url = "http://$host:$port$address?${args.joinToString("&") { "${it.first}=${it.second}" }}"
        return khttp.get(url).text
    }
}