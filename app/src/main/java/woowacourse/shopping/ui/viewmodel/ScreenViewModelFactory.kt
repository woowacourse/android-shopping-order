package woowacourse.shopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.AppContainer
import woowacourse.shopping.ui.viewmodel.ShoppingCartRecommendViewModel

class ScreenViewModelFactory(
    private val appContainer: AppContainer,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        when {
            modelClass.isAssignableFrom(ProductListViewModel::class.java) ->
                ProductListViewModel(
                    shoppingCartRepository = appContainer.shoppingCartRepository,
                    shoppingItemRepository = appContainer.shoppingItemRepository,
                    visitStore = appContainer.visitStore,
                    networkStatusMonitor = appContainer.networkStatusMonitor,
                ) as T

            modelClass.isAssignableFrom(DetailProductViewModel::class.java) ->
                DetailProductViewModel(
                    shoppingCartRepository = appContainer.shoppingCartRepository,
                    shoppingItemRepository = appContainer.shoppingItemRepository,
                    visitStore = appContainer.visitStore,
                ) as T

            modelClass.isAssignableFrom(ShoppingCartItemViewModel::class.java) ->
                ShoppingCartItemViewModel(
                    shoppingCartRepository = appContainer.shoppingCartRepository,
                    shoppingItemRepository = appContainer.shoppingItemRepository,
                ) as T

            modelClass.isAssignableFrom(ShoppingCartRecommendViewModel::class.java) ->
                ShoppingCartRecommendViewModel(
                    shoppingItemRepository = appContainer.shoppingItemRepository,
                    visitStore = appContainer.visitStore,
                ) as T

            else -> throw IllegalArgumentException("지원하지 않는 화면 ViewModel: ${modelClass.name}")
        }
}
