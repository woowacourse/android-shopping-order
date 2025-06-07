package woowacourse.shopping.view.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.repository.CartProductRepository
import woowacourse.shopping.domain.repository.CouponRepository

class PaymentViewModelFactory(
    private val cartProductRepository: CartProductRepository,
    private val couponRepository: CouponRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PaymentViewModel::class.java)) {
            return PaymentViewModel(
                cartProductRepository,
                couponRepository,
            ) as T
        }
        throw IllegalArgumentException(INVALID_VIEWMODEL_CLASS)
    }

    companion object {
        private const val INVALID_VIEWMODEL_CLASS: String = "생성할 수 없는 ViewModel 클래스입니다"
    }
}
