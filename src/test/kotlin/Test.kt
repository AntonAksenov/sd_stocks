import account.AccountService
import org.junit.jupiter.api.Test
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
class Test {
    @Container
    val container: GenericContainer<*> =
        GenericContainer("stocks:latest")
            .withExposedPorts(8080)
            .also { it.start() }
    private var address: String = container.host
    private val port: Int = container.getMappedPort(8080)

    init {
        val accountService = AccountService(address, port)
        accountService.request("/createStock", Pair("name", "apple"), Pair("price", "1"), Pair("amount", "1"))
    }

    @Test
    fun testRegister() {
        val accountService = AccountService(address, port)
        assert(accountService.register(1, "name") == "Success")
    }

    @Test
    fun testRegisterSame() {
        val accountService = AccountService(address, port)
        assert(accountService.register(2, "name") == "Success")
        assert(accountService.register(2, "name") != "Success")
    }

    @Test
    fun testBuyWithNoMoney() {
        val accountService = AccountService(address, port)
        assert(accountService.register(3, "name") == "Success")
        assert(accountService.buyStock(3, "apple", 1) != "Success")
    }

    @Test
    fun testBuyWithMoney() {
        val accountService = AccountService(address, port)
        assert(accountService.register(4, "name") == "Success")
        accountService.addMoney(4, 10)
        assert(accountService.buyStock(4, "apple", 1) == "Success")
    }

    @Test
    fun testBuyWithNoStock() {
        val accountService = AccountService(address, port)
        assert(accountService.register(5, "name") == "Success")
        accountService.addMoney(5, 10)
        assert(accountService.buyStock(5, "apple", 1) == "Success")
        assert(accountService.buyStock(5, "apple", 1) != "Success")
    }

    @Test
    fun testSellWithNoStock() {
        val accountService = AccountService(address, port)
        assert(accountService.register(6, "name") == "Success")
        accountService.addMoney(6, 10)
        assert(accountService.sellStock(6, "apple", 1) != "Success")
    }
}