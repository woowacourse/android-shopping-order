package woowacourse.shopping.di

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import woowacourse.shopping.data.local.datastore.DataStoreVisitStore
import woowacourse.shopping.data.local.datastore.VisitStore
import woowacourse.shopping.data.local.room.shoppingItem.ShoppingItemDao
import woowacourse.shopping.data.local.room.shoppingcart.ShoppingCartDao
import woowacourse.shopping.data.remote.retrofit.RetrofitService
import woowacourse.shopping.data.remote.retrofit.repository.OrderRetrofitRepository
import woowacourse.shopping.data.remote.retrofit.repository.ProductRetrofitRepository
import woowacourse.shopping.data.remote.retrofit.repository.ShoppingCartRetrofitRepository
import woowacourse.shopping.data.repository.RoomShoppingCartRepository
import woowacourse.shopping.data.repository.RoomShoppingItemRepository
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
        RoomShoppingItemRepository(
            shoppingItemDao = shoppingItemDao,
            scope = applicationScope,
        )

    val shoppingCartRepository: ShoppingCartRepository =
        RoomShoppingCartRepository(
            shoppingCartDao = shoppingCartDao,
            shoppingItemRepository = shoppingItemRepository,
            shoppingCartRetrofitRepository = ShoppingCartRetrofitRepository(retrofitService.shoppingCartApiService),
        )

    val productRepository =
        ProductRetrofitRepository(retrofitService.productApiService)

    val orderRepository =
        OrderRetrofitRepository(retrofitService.orderApiService)

    val visitStore: VisitStore =
        DataStoreVisitStore(
            context = context,
            scope = applicationScope,
        )
}
