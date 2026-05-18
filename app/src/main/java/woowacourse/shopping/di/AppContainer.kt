package woowacourse.shopping.di

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import woowacourse.shopping.data.local.datastore.DataStoreVisitStore
import woowacourse.shopping.data.local.datastore.VisitStore
import woowacourse.shopping.data.network.AndroidNetworkStatusMonitor
import woowacourse.shopping.data.network.NetworkStatusMonitor
import woowacourse.shopping.data.remote.retrofit.sync.RemoteShoppingStateSyncer
import woowacourse.shopping.data.repository.InMemoryShoppingCartRepository
import woowacourse.shopping.data.repository.InMemoryShoppingItemRepository
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.domain.repository.ShoppingItemRepository

class AppContainer(
    context: Context,
    applicationScope: CoroutineScope,
) {
    val shoppingItemRepository: ShoppingItemRepository =
        InMemoryShoppingItemRepository()

    val shoppingCartRepository: ShoppingCartRepository =
        InMemoryShoppingCartRepository(shoppingItemRepository = shoppingItemRepository)

    val visitStore: VisitStore =
        DataStoreVisitStore(
            context = context,
            scope = applicationScope,
        )

    val networkStatusMonitor: NetworkStatusMonitor =
        AndroidNetworkStatusMonitor(
            context = context,
            scope = applicationScope,
        )

    val remoteShoppingStateSyncer: RemoteShoppingStateSyncer =
        RemoteShoppingStateSyncer(
            shoppingCartRepository = shoppingCartRepository,
            shoppingItemRepository = shoppingItemRepository,
        )
}
