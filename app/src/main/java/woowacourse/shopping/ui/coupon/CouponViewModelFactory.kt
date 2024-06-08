package woowacourse.shopping.ui.coupon

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository

class CouponViewModelFactory(
    private val couponRepository: CouponRepository,
    private val orderRepository: OrderRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CouponViewModel(
            couponRepository,
            orderRepository
        ) as T
    }
}
