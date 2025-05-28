package woowacourse.shopping

import android.content.Context
import woowacourse.shopping.data.datasource.CartDataSource
import woowacourse.shopping.data.datasource.CartDataSource2
import woowacourse.shopping.data.datasource.HistoryDataSource
import woowacourse.shopping.data.datasource.ProductsDataSource
import woowacourse.shopping.data.datasource.ProductsDataSource2
import woowacourse.shopping.data.db.PetoMarketDatabase
import woowacourse.shopping.data.network.MockingServer
import woowacourse.shopping.data.network.RetrofitProvider
import woowacourse.shopping.data.network.service.CartService
import woowacourse.shopping.data.network.service.ProductService
import woowacourse.shopping.data.repository.CartRepositoryImpl
import woowacourse.shopping.data.repository.DefaultCartRepository
import woowacourse.shopping.data.repository.DefaultProductRepository
import woowacourse.shopping.data.repository.HistoryRepositoryImpl
import woowacourse.shopping.data.repository.ProductRepositoryImpl
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.HistoryRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.view.loader.CartLoader
import woowacourse.shopping.view.loader.HistoryLoader
import kotlin.getValue

class AppContainer(
    context: Context,
) {
    private val db = PetoMarketDatabase.getInstance(context)

    private val cartDao = db.cartDao()

    private val historyDao = db.historyDao()

    private val cartDataSource = CartDataSource(cartDao)

    private val productService = MockingServer()

    private val productDataSource = ProductsDataSource(productService)

    private val historyDataSource = HistoryDataSource(historyDao)

    private val productService2: ProductService = RetrofitProvider.productService

    private val cartService: CartService = RetrofitProvider.cartService

    private val cartDataSource2 = CartDataSource2(cartService)

    private val productsDataSource2 = ProductsDataSource2(productService2)

    val productRepository2 = DefaultProductRepository(productsDataSource2)

    val productRepository: ProductRepository by lazy { ProductRepositoryImpl(productDataSource) }

    val cartRepository: CartRepository by lazy { CartRepositoryImpl(cartDataSource) }

    val historyRepository: HistoryRepository by lazy { HistoryRepositoryImpl(historyDataSource) }

    val cartRepository2 = DefaultCartRepository(cartDataSource2)

    val historyLoader = HistoryLoader(productRepository, historyRepository)

    val cartLoader = CartLoader(cartRepository, productRepository)
}
