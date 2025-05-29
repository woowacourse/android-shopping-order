package woowacourse.shopping

import android.content.Context
import woowacourse.shopping.data.datasource.CartDataSource
import woowacourse.shopping.data.datasource.HistoryDataSource
import woowacourse.shopping.data.datasource.ProductsDataSource
import woowacourse.shopping.data.db.PetoMarketDatabase
import woowacourse.shopping.data.network.RetrofitProvider
import woowacourse.shopping.data.network.service.CartService
import woowacourse.shopping.data.network.service.ProductService
import woowacourse.shopping.data.repository.DefaultCartRepository
import woowacourse.shopping.data.repository.DefaultProductRepository
import woowacourse.shopping.data.repository.HistoryRepositoryImpl
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.HistoryRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.view.loader.HistoryLoader
import kotlin.getValue

class AppContainer(
    context: Context,
) {
    private val db = PetoMarketDatabase.getInstance(context)

    private val historyDao = db.historyDao()

    private val historyDataSource = HistoryDataSource(historyDao)

    private val productService: ProductService = RetrofitProvider.productService

    private val cartService: CartService = RetrofitProvider.cartService

    private val cartDataSource = CartDataSource(cartService)

    private val productsDataSource = ProductsDataSource(productService)

    val productRepository: ProductRepository = DefaultProductRepository(productsDataSource)

    val historyRepository: HistoryRepository by lazy { HistoryRepositoryImpl(historyDataSource) }

    val cartRepository: CartRepository = DefaultCartRepository(cartDataSource)

    val historyLoader = HistoryLoader(productRepository, historyRepository)
}
