package woowacourse.shopping.di

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import woowacourse.shopping.data.datasource.remote.cart.RetrofitShoppingCartRemoteDataSource
import woowacourse.shopping.data.datasource.remote.order.RetrofitOrderRemoteDataSource
import woowacourse.shopping.data.datasource.remote.product.RetrofitProductRemoteDataSource
import woowacourse.shopping.data.local.datastore.DataStoreVisitStore
import woowacourse.shopping.data.local.datastore.VisitStore
import woowacourse.shopping.data.remote.retrofit.RetrofitService
import woowacourse.shopping.data.repository.cart.ShoppingCartRepositoryImpl
import woowacourse.shopping.data.repository.item.ShoppingItemRepositoryImpl
import woowacourse.shopping.data.repository.order.OrderRepositoryImpl
import woowacourse.shopping.data.repository.product.ProductRepositoryImpl
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.domain.repository.ShoppingItemRepository

class AppContainer(
    context: Context,
    applicationScope: CoroutineScope,
    retrofitService: RetrofitService,
) {
    val shoppingItemRepository: ShoppingItemRepository = ShoppingItemRepositoryImpl()

    val shoppingCartRepository: ShoppingCartRepository =
        ShoppingCartRepositoryImpl(
            shoppingItemRepository = shoppingItemRepository,
            shoppingCartRemoteDataSource =
                RetrofitShoppingCartRemoteDataSource(
                    retrofitService.shoppingCartApiService,
                ),
        )

    val productRepository: ProductRepository =
        ProductRepositoryImpl(
            productRemoteDataSource =
                RetrofitProductRemoteDataSource(
                    retrofitService.productApiService,
                ),
        )

    val orderRepository: OrderRepository =
        OrderRepositoryImpl(
            orderRemoteDataSource =
                RetrofitOrderRemoteDataSource(
                    retrofitService.orderApiService,
                ),
        )

    val visitStore: VisitStore =
        DataStoreVisitStore(
            context = context,
            scope = applicationScope,
        )
}
