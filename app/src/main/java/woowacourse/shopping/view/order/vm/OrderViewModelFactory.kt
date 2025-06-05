package woowacourse.shopping.view.order.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.repository.CouponRepository

class OrderViewModelFactory(
    private val couponRepository: CouponRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderViewModel::class.java)) {
            return OrderViewModel(couponRepository) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}
