package woowacourse.shopping

import android.content.Context
import woowacourse.shopping.data.ApiCallbackHandler
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
import woowacourse.shopping.view.loader.HistoryLoader

class AppContainer(
    context: Context,
) {
    private val callBackHandler = ApiCallbackHandler()
    private val db = PetoMarketDatabase.getInstance(context)

    private val historyDao = db.historyDao()

    private val historyDataSource = DefaultHistoryDataSource(historyDao)

    private val productService: ProductService = RetrofitProvider.productService

    private val cartService: CartService = RetrofitProvider.cartService

    private val cartDataSource = CartDataSource(cartService, callBackHandler)

    private val productsDataSource = ProductsDataSource(productService, callBackHandler)

    val productRepository: ProductRepository =
        DefaultProductRepository(productsDataSource)

    val historyRepository: HistoryRepository = DefaultHistoryRepository(historyDataSource)

    val cartRepository: CartRepository = DefaultCartRepository(cartDataSource)

    val historyLoader = HistoryLoader(productRepository, historyRepository)
}
