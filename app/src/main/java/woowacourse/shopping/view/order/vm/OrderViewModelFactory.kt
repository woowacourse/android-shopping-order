package woowacourse.shopping.view.order.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.coupon.CouponValidate
import woowacourse.shopping.domain.repository.CouponRepository

class OrderViewModelFactory(
    private val couponRepository: CouponRepository,
    private val couponValidator: CouponValidate,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderViewModel::class.java)) {
            return OrderViewModel(couponRepository, couponValidator) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}
