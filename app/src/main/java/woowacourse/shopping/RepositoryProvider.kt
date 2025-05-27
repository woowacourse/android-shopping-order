package woowacourse.shopping

import android.app.Application
import woowacourse.shopping.data.repository.CartItemsRepositoryImpl
import woowacourse.shopping.data.repository.ProductsRepositoryImpl
import woowacourse.shopping.data.repository.ViewedItemRepositoryImpl
import woowacourse.shopping.data.source.local.recent.ViewedItemDatabase
import woowacourse.shopping.data.source.remote.Client.getCartRetrofitService
import woowacourse.shopping.data.source.remote.Client.getProductRetrofitService
import woowacourse.shopping.data.source.remote.cart.CartItemsRemoteDataSource
import woowacourse.shopping.data.source.remote.products.ProductsRemoteDataSource
import woowacourse.shopping.domain.repository.CartItemRepository
import woowacourse.shopping.domain.repository.ProductsRepository
import woowacourse.shopping.domain.repository.ViewedItemRepository

object RepositoryProvider {
    lateinit var productsRepository: ProductsRepository
    lateinit var cartItemRepository: CartItemRepository
    lateinit var viewedItemRepository: ViewedItemRepository

    fun init(application: Application) {
        productsRepository =
            ProductsRepositoryImpl(ProductsRemoteDataSource(getProductRetrofitService))
        cartItemRepository =
            CartItemsRepositoryImpl(CartItemsRemoteDataSource(getCartRetrofitService))
        viewedItemRepository =
            ViewedItemRepositoryImpl(ViewedItemDatabase.getInstance(application).viewedItemDao())
    }
}