package woowacourse.shopping.domain.repository.coupon

import woowacourse.shopping.data.common.ResponseResult
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.ui.model.OrderInformation

interface CouponRepository {
    suspend fun loadCoupons(orderInformation: OrderInformation): ResponseResult<List<Coupon>>

    fun calculateDiscountAmount(
        orderInformation: OrderInformation,
        selectedCouponId: Long,
        isChecked: Boolean,
    ): Int

    fun calculateTotalAmount(
        orderInformation: OrderInformation,
        selectedCouponId: Long,
        isChecked: Boolean,
    ): Int

    fun calculateShippingFee(
        orderInformation: OrderInformation,
        selectedCouponId: Long,
        isChecked: Boolean,
    ): Int
}
