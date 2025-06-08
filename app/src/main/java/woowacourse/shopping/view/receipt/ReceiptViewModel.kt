package woowacourse.shopping.view.receipt

import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.cart.repository.CartRepository
import woowacourse.shopping.data.cart.repository.DefaultCartRepository
import woowacourse.shopping.data.coupon.repository.CouponRepository
import woowacourse.shopping.data.coupon.repository.DefaultCouponRepository

class ReceiptViewModel(
    private val couponRepository: CouponRepository = DefaultCouponRepository(),
    private val cartRepository: CartRepository = DefaultCartRepository()
) : ViewModel() {

}