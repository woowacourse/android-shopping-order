package woowacourse.shopping.view.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.model.CartItemDomain
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository

class OrderViewModelFactory(
    private val couponRepository: CouponRepository,
    private val orderRepository: OrderRepository,
    private val cartItems: List<CartItemDomain> = emptyList(),
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OrderViewModel(
                couponRepository = couponRepository,
                orderRepository = orderRepository,
                cartItems = cartItems
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
