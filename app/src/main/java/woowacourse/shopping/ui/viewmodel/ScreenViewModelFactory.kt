package woowacourse.shopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.network.NetworkStatusMonitor
import woowacourse.shopping.repository.ShoppingCartRepository
import woowacourse.shopping.repository.ShoppingItemRepository
import woowacourse.shopping.storage.datastore.VisitStore

class ScreenViewModelFactory(
    private val shoppingCartRepository: ShoppingCartRepository,
    private val shoppingItemRepository: ShoppingItemRepository,
    private val visitStore: VisitStore,
    private val networkStatusMonitor: NetworkStatusMonitor,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        when {
            modelClass.isAssignableFrom(ProductListViewModel::class.java) ->
                ProductListViewModel(
                    shoppingCartRepository = shoppingCartRepository,
                    shoppingItemRepository = shoppingItemRepository,
                    visitStore = visitStore,
                    networkStatusMonitor = networkStatusMonitor,
                ) as T

            modelClass.isAssignableFrom(DetailProductViewModel::class.java) ->
                DetailProductViewModel(
                    shoppingCartRepository = shoppingCartRepository,
                    shoppingItemRepository = shoppingItemRepository,
                    visitStore = visitStore,
                ) as T

            modelClass.isAssignableFrom(ShoppingCartItemViewModel::class.java) ->
                ShoppingCartItemViewModel(
                    shoppingCartRepository = shoppingCartRepository,
                    shoppingItemRepository = shoppingItemRepository,
                ) as T

            else -> throw IllegalArgumentException("지원하지 않는 화면 ViewModel: ${modelClass.name}")
        }
}
