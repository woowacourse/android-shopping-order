package woowacourse.shopping.ui.payment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.data.coupon.CouponRepository

class PaymentViewModelFactory(
    private val couponRepository: CouponRepository,
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PaymentViewModel(couponRepository) as T
    }
}
