package woowacourse.shopping.ui.payment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.data.cart.CartRepository
import woowacourse.shopping.data.coupon.CouponRepository

class PaymentViewModelFactory(
    private val orderProductIds: List<Long>,
    private val cartRepository: CartRepository,
    private val couponRepository: CouponRepository,
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PaymentViewModel(orderProductIds, cartRepository, couponRepository) as T
    }
}
