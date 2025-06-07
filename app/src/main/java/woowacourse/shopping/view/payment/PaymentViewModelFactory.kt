package woowacourse.shopping.view.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository

class PaymentViewModelFactory(
    private val couponRepository: CouponRepository,
    private val orderRepository: OrderRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PaymentViewModel::class.java)) {
            return PaymentViewModel(
                couponRepository,
                orderRepository,
            ) as T
        }
        throw IllegalArgumentException(INVALID_VIEWMODEL_CLASS)
    }

    companion object {
        private const val INVALID_VIEWMODEL_CLASS: String = "생성할 수 없는 ViewModel 클래스입니다"
    }
}
