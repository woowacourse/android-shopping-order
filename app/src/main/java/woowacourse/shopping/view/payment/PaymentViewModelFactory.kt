package woowacourse.shopping.view.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.repository.CouponRepository

class PaymentViewModelFactory(
    private val couponRepository: CouponRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PaymentViewModel::class.java)) {
            return PaymentViewModel(couponRepository) as T
        }
        throw IllegalArgumentException()
    }
}
