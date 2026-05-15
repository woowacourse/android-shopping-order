package woowacourse.shopping.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.data.remote.retrofit.RetrofitService
import woowacourse.shopping.data.remote.retrofit.repository.OrderRetrofitRepository
import woowacourse.shopping.data.remote.retrofit.repository.ProductRetrofitRepository
import woowacourse.shopping.data.remote.retrofit.repository.ShoppingCartRetrofitRepository
import woowacourse.shopping.ui.cart.OrderViewModel
import woowacourse.shopping.ui.cart.ShoppingCartViewModel
import woowacourse.shopping.ui.productlist.ProductViewModel

class ApiViewModelFactory(
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
            modelClass.isAssignableFrom(ProductViewModel::class.java) ->
                ProductViewModel(productRetrofitRepository = productRetrofitRepository) as T

            modelClass.isAssignableFrom(ShoppingCartViewModel::class.java) ->
                ShoppingCartViewModel(
                    shoppingCartRetrofitRepository = shoppingCartRetrofitRepository,
                ) as T

            modelClass.isAssignableFrom(OrderViewModel::class.java) ->
                OrderViewModel(orderRetrofitRepository = orderRetrofitRepository) as T

            else -> throw IllegalArgumentException("지원하지 않는 API ViewModel: ${modelClass.name}")
        }
}
