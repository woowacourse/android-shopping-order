package woowacourse.shopping.data.coupon.remote

import woowacourse.shopping.data.common.ApiResponseHandler.handleResponseResult
import woowacourse.shopping.data.common.ResponseResult
import woowacourse.shopping.data.coupon.remote.datasource.CouponDataSource
import woowacourse.shopping.data.coupon.remote.dto.CouponDto.Companion.toDomain
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.FreeShippingCoupon
import woowacourse.shopping.domain.repository.coupon.CouponRepository
import woowacourse.shopping.ui.model.OrderInformation

class CouponRemoteRepository(
    private val couponDataSource: CouponDataSource,
) : CouponRepository {
    private var validCoupons: List<Coupon> = emptyList()

    override suspend fun loadCoupons(orderInformation: OrderInformation): ResponseResult<List<Coupon>> =
        handleResponseResult(couponDataSource.loadCoupons()) { response ->
            validCoupons = response.map { it.toDomain() }.filter { it.isAvailability(orderInformation.cartItems) }
            ResponseResult.Success(validCoupons)
        }

    override fun calculateDiscountAmount(
        orderInformation: OrderInformation,
        selectedCouponId: Long,
        isChecked: Boolean,
    ): Int {
        val selectedCoupon =
            validCoupons.find {
                it.id == selectedCouponId
            } ?: throw NoSuchElementException(INVALID_COUPON.format(selectedCouponId))
        return if (isChecked) {
            selectedCoupon.calculateDiscountAmount(orderInformation.cartItems)
        } else {
            NO_DISCOUNT
        }
    }

    override fun calculateTotalAmount(
        orderInformation: OrderInformation,
        selectedCouponId: Long,
        isChecked: Boolean,
    ): Int {
        val orderAmount = orderInformation.calculateDefaultTotalAmount()
        val discountAmount = calculateDiscountAmount(orderInformation, selectedCouponId, isChecked)
        return orderAmount + discountAmount
    }

    override fun calculateShippingFee(
        orderInformation: OrderInformation,
        selectedCouponId: Long,
        isChecked: Boolean,
    ): Int {
        val selectedCoupon =
            validCoupons.find {
                it.id == selectedCouponId
            } ?: throw NoSuchElementException(INVALID_COUPON.format(selectedCouponId))
        val isSelectedFreeShipping = selectedCoupon.discountType == FreeShippingCoupon.TYPE
        return if (isSelectedFreeShipping) orderInformation.determineShippingFee(isChecked) else OrderInformation.SHIPPING_FEE
    }

    companion object {
        const val NO_DISCOUNT = 0
        const val INVALID_COUPON = "id가 %d인 쿠폰을 찾을 수 없습니다."
    }
}
