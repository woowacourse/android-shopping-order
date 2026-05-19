package woowacourse.shopping.di

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import woowacourse.shopping.data.local.datastore.DataStoreVisitStore
import woowacourse.shopping.data.local.datastore.VisitStore
import woowacourse.shopping.data.local.room.shoppingItem.ShoppingItemDao
import woowacourse.shopping.data.local.room.shoppingcart.ShoppingCartDao
import woowacourse.shopping.data.remote.retrofit.sync.RemoteShoppingStateSyncer
import woowacourse.shopping.data.repository.RoomShoppingCartRepository
import woowacourse.shopping.data.repository.RoomShoppingItemRepository
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.domain.repository.ShoppingItemRepository

class AppContainer(
    context: Context,
    shoppingItemDao: ShoppingItemDao,
    shoppingCartDao: ShoppingCartDao,
    applicationScope: CoroutineScope,
) {
    val shoppingItemRepository: ShoppingItemRepository =
        RoomShoppingItemRepository(
            shoppingItemDao = shoppingItemDao,
            scope = applicationScope,
        )

    val shoppingCartRepository: ShoppingCartRepository =
        RoomShoppingCartRepository(
            shoppingCartDao = shoppingCartDao,
        )

    val visitStore: VisitStore =
        DataStoreVisitStore(
            context = context,
            scope = applicationScope,
        )

    val remoteShoppingStateSyncer: RemoteShoppingStateSyncer =
        RemoteShoppingStateSyncer(
            shoppingCartRepository = shoppingCartRepository,
            shoppingItemRepository = shoppingItemRepository,
        )
}
