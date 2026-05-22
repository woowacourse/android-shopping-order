package woowacourse.shopping.di

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import woowacourse.shopping.data.datasource.local.cart.RoomShoppingCartLocalDataSource
import woowacourse.shopping.data.datasource.remote.order.OrderRemoteDataSource
import woowacourse.shopping.data.datasource.remote.product.ProductRemoteDataSource
import woowacourse.shopping.data.datasource.remote.cart.RetrofitShoppingCartRemoteDataSource
import woowacourse.shopping.data.local.datastore.DataStoreVisitStore
import woowacourse.shopping.data.local.datastore.VisitStore
import woowacourse.shopping.data.local.room.shoppingItem.ShoppingItemDao
import woowacourse.shopping.data.local.room.shoppingcart.ShoppingCartDao
import woowacourse.shopping.data.remote.retrofit.RetrofitService
import woowacourse.shopping.data.repository.cart.ShoppingCartRepositoryImpl
import woowacourse.shopping.data.repository.item.ShoppingItemRepositoryImpl
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.domain.repository.ShoppingItemRepository

class AppContainer(
    context: Context,
    shoppingItemDao: ShoppingItemDao,
    shoppingCartDao: ShoppingCartDao,
    applicationScope: CoroutineScope,
    retrofitService: RetrofitService,
) {
    val shoppingItemRepository: ShoppingItemRepository =
        ShoppingItemRepositoryImpl(
            shoppingItemDao = shoppingItemDao,
            scope = applicationScope,
        )

    val shoppingCartRepository: ShoppingCartRepository =
        ShoppingCartRepositoryImpl(
            shoppingCartLocalDataSource =
                RoomShoppingCartLocalDataSource(
                    shoppingCartDao = shoppingCartDao,
                ),
            shoppingItemRepository = shoppingItemRepository,
            shoppingCartRemoteDataSource =
                RetrofitShoppingCartRemoteDataSource(
                    retrofitService.shoppingCartApiService,
                ),
        )

    val productRepository =
        ProductRemoteDataSource(retrofitService.productApiService)

    val orderRepository =
        OrderRemoteDataSource(retrofitService.orderApiService)

    val visitStore: VisitStore =
        DataStoreVisitStore(
            context = context,
            scope = applicationScope,
        )
}
