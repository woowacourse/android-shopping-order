package woowacourse.shopping.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.data.remote.retrofit.RetrofitService
import woowacourse.shopping.data.remote.retrofit.repository.OrderRetrofitRepository
import woowacourse.shopping.data.remote.retrofit.repository.ProductRetrofitRepository
import woowacourse.shopping.data.remote.retrofit.repository.ShoppingCartRetrofitRepository
import woowacourse.shopping.ui.cart.OrderViewModel
import woowacourse.shopping.ui.cart.ShoppingCartItemViewModel
import woowacourse.shopping.ui.cart.ShoppingCartViewModel
import woowacourse.shopping.ui.detail.DetailProductViewModel
import woowacourse.shopping.ui.productlist.ProductListViewModel
import woowacourse.shopping.ui.recommend.ShoppingCartRecommendViewModel

class AppViewModelFactory(
    private val appContainer: AppContainer,
    retrofitService: RetrofitService,
) : ViewModelProvider.Factory {
    private val productRetrofitRepository: ProductRetrofitRepository =
        ProductRetrofitRepository(retrofitService.productApiService)
    private val shoppingCartRetrofitRepository: ShoppingCartRetrofitRepository =
        ShoppingCartRetrofitRepository(retrofitService.shoppingCartApiService)
    private val orderRetrofitRepository: OrderRetrofitRepository =
        OrderRetrofitRepository(retrofitService.orderApiService)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        when {
            modelClass.isAssignableFrom(ProductListViewModel::class.java) ->
                ProductListViewModel(
                    shoppingItemRepository = appContainer.shoppingItemRepository,
                    visitStore = appContainer.visitStore,
                    productRetrofitRepository = productRetrofitRepository,
                ) as T

            modelClass.isAssignableFrom(DetailProductViewModel::class.java) ->
                DetailProductViewModel(
                    shoppingCartRepository = appContainer.shoppingCartRepository,
                    shoppingItemRepository = appContainer.shoppingItemRepository,
                    visitStore = appContainer.visitStore,
                    productRetrofitRepository = productRetrofitRepository,
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

            modelClass.isAssignableFrom(ShoppingCartViewModel::class.java) ->
                ShoppingCartViewModel(
                    shoppingCartRetrofitRepository = shoppingCartRetrofitRepository,
                ) as T

            modelClass.isAssignableFrom(OrderViewModel::class.java) ->
                OrderViewModel(orderRetrofitRepository = orderRetrofitRepository) as T

            else -> throw IllegalArgumentException("지원하지 않는 ViewModel: ${modelClass.name}")
        }
}
