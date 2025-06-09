package woowacourse.shopping.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.di.RepositoryProvider
import woowacourse.shopping.product.catalog.ProductUiModel

class OrderViewModelFactory(
    private val products: Array<ProductUiModel>,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OrderViewModel(
                couponRepository = RepositoryProvider.provideCouponRepository(),
                orderRepository = RepositoryProvider.provideOrderRepository(),
                products = products,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
