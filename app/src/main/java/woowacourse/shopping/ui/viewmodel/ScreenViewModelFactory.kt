package woowacourse.shopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.AppContainer
import woowacourse.shopping.backend.retrofit.RetrofitService
import woowacourse.shopping.backend.retrofit.repository.CouponRetrofitRepository
import woowacourse.shopping.backend.retrofit.repository.OrderRetrofitRepository
import woowacourse.shopping.backend.retrofit.repository.ProductRetrofitRepository
import woowacourse.shopping.backend.retrofit.repository.ShoppingCartRetrofitRepository
import woowacourse.shopping.backend.retrofit.viewmodel.OrderViewModel
import woowacourse.shopping.backend.retrofit.viewmodel.ProductViewModel
import woowacourse.shopping.backend.retrofit.viewmodel.ShoppingCartViewModel

class ScreenViewModelFactory(
    private val appContainer: AppContainer,
    val retrofitService: RetrofitService,
) : ViewModelProvider.Factory {
    private val productRetrofitRepository: ProductRetrofitRepository =
        ProductRetrofitRepository(retrofitService.productApiService)
    private val shoppingCartRetrofitRepository: ShoppingCartRetrofitRepository =
        ShoppingCartRetrofitRepository(retrofitService.shoppingCartApiService)
    private val orderRetrofitRepository: OrderRetrofitRepository =
        OrderRetrofitRepository(retrofitService.orderApiService)
    private val couponRetrofitRepository: CouponRetrofitRepository =
        CouponRetrofitRepository(retrofitService.couponApiService)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        when {
            modelClass.isAssignableFrom(ProductListViewModel::class.java) ->
                ProductListViewModel(
                    productRetrofitRepository = productRetrofitRepository,
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

            modelClass.isAssignableFrom(ProductViewModel::class.java) ->
                ProductViewModel(productRetrofitRepository = productRetrofitRepository) as T

            modelClass.isAssignableFrom(ShoppingCartViewModel::class.java) ->
                ShoppingCartViewModel(
                    shoppingCartRetrofitRepository = shoppingCartRetrofitRepository,
                ) as T

            modelClass.isAssignableFrom(OrderViewModel::class.java) ->
                OrderViewModel(orderRetrofitRepository = orderRetrofitRepository) as T

            modelClass.isAssignableFrom(CouponViewModel::class.java) ->
                CouponViewModel(couponRetrofitRepository = couponRetrofitRepository) as T

            else -> throw IllegalArgumentException("지원하지 않는 API ViewModel: ${modelClass.name}")
        }
}
