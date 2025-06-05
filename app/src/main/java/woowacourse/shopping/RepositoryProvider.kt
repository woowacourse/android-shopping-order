package woowacourse.shopping

import android.content.Context
import woowacourse.shopping.data.repository.CartItemsRepositoryImpl
import woowacourse.shopping.data.repository.ProductsRepositoryImpl
import woowacourse.shopping.data.repository.ViewedItemRepositoryImpl
import woowacourse.shopping.data.source.local.cart.CartItemsLocalDataSource
import woowacourse.shopping.data.source.local.recent.ViewedItemDatabase
import woowacourse.shopping.data.source.remote.Client.getCartApiService
import woowacourse.shopping.data.source.remote.Client.getProductsApiService
import woowacourse.shopping.data.source.remote.cart.CartItemsRemoteDataSource
import woowacourse.shopping.data.source.remote.products.ProductsRemoteDataSource
import woowacourse.shopping.domain.repository.CartItemRepository
import woowacourse.shopping.domain.repository.ProductsRepository
import woowacourse.shopping.domain.repository.ViewedItemRepository

object RepositoryProvider {
    lateinit var productsRepository: ProductsRepository
        private set
    lateinit var cartItemRepository: CartItemRepository
        private set
    lateinit var viewedItemRepository: ViewedItemRepository
        private set

    fun init(context: Context) {
        productsRepository =
            ProductsRepositoryImpl(
                ProductsRemoteDataSource(getProductsApiService),
                CartItemsRemoteDataSource(getCartApiService),
            )
        cartItemRepository =
            CartItemsRepositoryImpl(
                CartItemsRemoteDataSource(getCartApiService),
                CartItemsLocalDataSource(),
            )
        viewedItemRepository =
            ViewedItemRepositoryImpl(ViewedItemDatabase.getInstance(context).viewedItemDao())
    }
}
