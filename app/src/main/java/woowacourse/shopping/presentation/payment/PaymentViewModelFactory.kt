package woowacourse.shopping.presentation.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.repository.CouponRepository

class PaymentViewModelFactory(private val couponRepositoryImpl: CouponRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PaymentViewModel(couponRepositoryImpl) as T
    }
}
