package woowacourse.shopping.data.coupon.repository

import woowacourse.shopping.data.coupon.dto.CouponResponse
import woowacourse.shopping.data.coupon.dto.CouponResponseItem
import woowacourse.shopping.data.coupon.source.CouponDataSource
import woowacourse.shopping.di.DataSourceModule
import woowacourse.shopping.domain.coupon.BoGoCoupon
import woowacourse.shopping.domain.coupon.Coupon
import woowacourse.shopping.domain.coupon.FixedCoupon
import woowacourse.shopping.domain.coupon.FreeShippingCoupon
import woowacourse.shopping.domain.coupon.MiracleSaleCoupon
import java.time.LocalDate
import java.time.LocalTime

class DefaultCouponRepository(
    private val couponDataSource: CouponDataSource = DataSourceModule.remoteCouponDataSource
) : CouponRepository {
    override suspend fun loadCoupons(): List<Coupon> {
        val couponResponse: CouponResponse? = couponDataSource.coupons()
        return couponResponse?.toDomain() ?: emptyList()
    }

    private fun CouponResponse.toDomain(): List<Coupon> {
        return this.mapNotNull { item: CouponResponseItem ->
            if (item.discountType == null ||
                item.id == null ||
                item.expirationDate == null ||
                item.discount == null ||
                item.minimumAmount == null ||
                item.buyQuantity == null ||
                item.getQuantity == null ||
                item.availableTime == null
            ) {
                null
            } else {
                when (item.discountType) {
                    "fixed" ->
                        FixedCoupon(
                            couponId = item.id,
                            expirationDate = LocalDate.parse(item.expirationDate),
                            disCountPrice = item.discount,
                            minimumOrderPrice = item.minimumAmount
                        )

                    "buyXgetY" ->
                        BoGoCoupon(
                            couponId = item.id,
                            expirationDate = LocalDate.parse(item.expirationDate),
                            buyQuantity = item.buyQuantity,
                            getQuantity = item.getQuantity
                        )

                    "freeShipping" ->
                        FreeShippingCoupon(
                            couponId = item.id,
                            expirationDate = LocalDate.parse(item.expirationDate),
                            minimumOrderPrice = item.minimumAmount
                        )

                    "percentage" ->
                        MiracleSaleCoupon(
                            couponId = item.id,
                            expirationDate = LocalDate.parse(item.expirationDate),
                            startHour = LocalTime.parse(item.availableTime.start),
                            endHour = LocalTime.parse(item.availableTime.end),
                            discountRate = item.discount.toDouble()
                        )

                    else -> null
                }
            }
        }
    }

}