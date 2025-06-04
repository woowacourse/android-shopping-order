package woowacourse.shopping

import android.content.Context
import woowacourse.shopping.data.NetworkResultHandler
import woowacourse.shopping.data.datasource.CartDataSource
import woowacourse.shopping.data.datasource.ProductsDataSource
import woowacourse.shopping.data.datasource.history.DefaultHistoryDataSource
import woowacourse.shopping.data.db.PetoMarketDatabase
import woowacourse.shopping.data.network.RetrofitProvider
import woowacourse.shopping.data.network.service.CartService
import woowacourse.shopping.data.network.service.ProductService
import woowacourse.shopping.data.repository.DefaultCartRepository
import woowacourse.shopping.data.repository.DefaultHistoryRepository
import woowacourse.shopping.data.repository.DefaultProductRepository
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.HistoryRepository
import woowacourse.shopping.domain.repository.ProductRepository

class AppContainer(
    context: Context,
) {
    private val networkResultHandler = NetworkResultHandler()
    private val db = PetoMarketDatabase.getInstance(context)

    private val historyDao = db.historyDao()

    private val defaultHistoryDataSource = DefaultHistoryDataSource(historyDao)

    private val productService: ProductService = RetrofitProvider.productService

    private val cartService: CartService = RetrofitProvider.cartService

    private val cartDataSource = CartDataSource(cartService, networkResultHandler)

    private val productsDataSource = ProductsDataSource(productService, networkResultHandler)

    val productRepository: ProductRepository =
        DefaultProductRepository(productsDataSource)

    val historyRepository: HistoryRepository = DefaultHistoryRepository(defaultHistoryDataSource)

    val cartRepository: CartRepository = DefaultCartRepository(cartDataSource)
}
