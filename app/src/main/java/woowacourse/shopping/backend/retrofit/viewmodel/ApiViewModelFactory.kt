package woowacourse.shopping.backend.retrofit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.backend.retrofit.RetrofitService
import woowacourse.shopping.backend.retrofit.repository.OrderRetrofitRepository
import woowacourse.shopping.backend.retrofit.repository.ProductRetrofitRepository
import woowacourse.shopping.backend.retrofit.repository.ShoppingCartRetrofitRepository

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
