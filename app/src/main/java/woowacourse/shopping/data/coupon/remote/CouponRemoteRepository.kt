package woowacourse.shopping.data.coupon.remote

import woowacourse.shopping.data.common.ApiResponseHandler.handleResponseResult
import woowacourse.shopping.data.common.ResponseResult
import woowacourse.shopping.data.coupon.remote.datasource.CouponDataSource
import woowacourse.shopping.data.coupon.remote.dto.CouponDto
import woowacourse.shopping.data.coupon.remote.dto.CouponDto.Companion.toBuyXGetYCoupon
import woowacourse.shopping.data.coupon.remote.dto.CouponDto.Companion.toFixedDiscountCoupon
import woowacourse.shopping.data.coupon.remote.dto.CouponDto.Companion.toFreeShippingCoupon
import woowacourse.shopping.data.coupon.remote.dto.CouponDto.Companion.toPercentageDiscountCoupon
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.CouponType
import woowacourse.shopping.domain.repository.coupon.CouponRepository

class CouponRemoteRepository(
    private val couponDataSource: CouponDataSource
): CouponRepository {
    override suspend fun loadCoupons(): ResponseResult<List<Coupon>> =
        handleResponseResult(couponDataSource.loadCoupons()) { response ->
            val coupons = response.coupons.map { it.toDomain() }
            ResponseResult.Success(coupons)
        }

    private fun CouponDto.toDomain(): Coupon =
        when (CouponType.from(discountType)) {
            CouponType.BUY_X_GET_Y -> toBuyXGetYCoupon()
            CouponType.FIXED_DISCOUNT -> toFixedDiscountCoupon()
            CouponType.FREE_SHIPPING -> toFreeShippingCoupon()
            CouponType.PERCENTAGE_DISCOUNT -> toPercentageDiscountCoupon()
        }
}
