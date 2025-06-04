package woowacourse.shopping.rule

import android.app.Activity
import androidx.test.core.app.ActivityScenario
import okhttp3.mockwebserver.MockWebServer
import org.junit.rules.ExternalResource
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.data.product.remote.service.ProductService
import woowacourse.shopping.data.product.repository.DefaultProductsRepository
import woowacourse.shopping.data.shoppingCart.remote.service.ShoppingCartService
import woowacourse.shopping.data.shoppingCart.repository.DefaultShoppingCartRepository
import woowacourse.shopping.fixture.FakeApiClient
import woowacourse.shopping.fixture.MockProductDispatcher
import woowacourse.shopping.fixture.MockShoppingCartDispatcher
import kotlin.concurrent.thread

class MockServerRule<T : Activity>(
    private val scenario: ActivityScenario<T>,
) : ExternalResource() {
    val mockProductServer = MockWebServer()
    val mockShoppingCartServer = MockWebServer()

    private val shoppingCartService
        get() =
            FakeApiClient.getApiClient(
                mockShoppingCartServer.url("/").toString(),
            ).create(ShoppingCartService::class.java)
    private val productService
        get() =
            FakeApiClient.getApiClient(
                mockProductServer.url("/").toString(),
            ).create(ProductService::class.java)

    override fun before() {
        mockProductServer.start()
        mockShoppingCartServer.start()
        mockProductServer.dispatcher =
            MockProductDispatcher(
                mockProductServer.url("/"),
            ).dispatcher
        mockShoppingCartServer.dispatcher =
            MockShoppingCartDispatcher(
                mockShoppingCartServer.url("/"),
            ).dispatcher

        scenario.onActivity { activity ->
            val app = activity.application as ShoppingApplication
            thread {
                app.productDatabase.clearAllTables()
            }
            DefaultShoppingCartRepository.initialize(shoppingCartService)
            DefaultProductsRepository.initialize(
                app.productDatabase.recentWatchingDao(),
                productService,
            )
        }
        scenario.recreate()
    }
}
