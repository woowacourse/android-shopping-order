package woowacourse.shopping.ui.payment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository

class PaymentViewModelFactory(
    private val cartItemIds: List<Int>,
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val couponRepository: CouponRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PaymentViewmodel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PaymentViewmodel(
                cartItemIds,
                cartRepository,
                orderRepository,
                couponRepository,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
