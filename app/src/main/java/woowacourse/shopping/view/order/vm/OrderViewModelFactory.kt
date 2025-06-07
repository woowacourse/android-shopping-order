package woowacourse.shopping.view.order.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.coupon.CouponApplierFactory
import woowacourse.shopping.domain.coupon.CouponValidate
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository

class OrderViewModelFactory(
    private val couponRepository: CouponRepository,
    private val orderRepository: OrderRepository,
    private val couponValidator: CouponValidate,
    private val couponApplierFactory: CouponApplierFactory,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderViewModel::class.java)) {
            return OrderViewModel(
                couponRepository,
                orderRepository,
                couponValidator,
                couponApplierFactory,
            ) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}
