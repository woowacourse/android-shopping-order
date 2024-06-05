package woowacourse.shopping.view.payment

import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.view.BaseViewModel

class PaymentViewModel(
    private val couponRepository: CouponRepository,
) : BaseViewModel(), OnclickPayment {
    override fun clickCoupon() {
        TODO("Not yet implemented")
    }

    override fun clickPayment() {
        TODO("Not yet implemented")
    }
}
