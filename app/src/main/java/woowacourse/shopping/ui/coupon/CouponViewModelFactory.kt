package woowacourse.shopping.ui.coupon

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository

class CouponViewModelFactory(
    private val selectedCartItemIds: List<Int>,
    private val cartRepository: CartRepository,
    private val couponRepository: CouponRepository,
    private val orderRepository: OrderRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CouponViewModel::class.java)) {
            return CouponViewModel(selectedCartItemIds, cartRepository, couponRepository, orderRepository) as T
        }
        throw IllegalArgumentException()
    }
}
