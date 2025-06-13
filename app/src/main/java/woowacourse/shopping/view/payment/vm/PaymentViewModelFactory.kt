package woowacourse.shopping.view.payment.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository

class PaymentViewModelFactory(
    private val couponRepository: CouponRepository,
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PaymentViewModel::class.java)) {
            return PaymentViewModel(
                couponRepository,
                cartRepository,
                orderRepository,
            ) as T
        }
        throw IllegalArgumentException()
    }
}
