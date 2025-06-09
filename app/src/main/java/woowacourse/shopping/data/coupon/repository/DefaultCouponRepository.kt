package woowacourse.shopping.data.coupon.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
    private val couponDataSource: CouponDataSource = DataSourceModule.remoteCouponDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CouponRepository {
    override suspend fun loadCoupons(): List<Coupon> = withContext(ioDispatcher) {
        val couponResponse: List<CouponResponseItem>? = couponDataSource.coupons()
        couponResponse?.toDomain() ?: emptyList()
    }

    private fun List<CouponResponseItem>.toDomain(): List<Coupon> {
        return this.mapNotNull { item: CouponResponseItem ->
            when (item.discountType) {
                "fixed" -> {
                    if (item.id == null || item.description == null || item.expirationDate == null || item.discount == 0 || item.minimumAmount == 0) {
                        return@mapNotNull null
                    }
                    FixedCoupon(
                        couponId = item.id,
                        description = item.description,
                        expirationDate = LocalDate.parse(item.expirationDate),
                        disCountPrice = item.discount,
                        minimumOrderPrice = item.minimumAmount
                    )
                }

                "buyXgetY" -> {
                    if (item.id == null || item.description == null || item.expirationDate == null || item.buyQuantity == 0 || item.getQuantity == 0) {
                        return@mapNotNull null
                    }
                    BoGoCoupon(
                        couponId = item.id,
                        description = item.description,
                        expirationDate = LocalDate.parse(item.expirationDate),
                        buyQuantity = item.buyQuantity,
                        getQuantity = item.getQuantity
                    )
                }

                "freeShipping" -> {
                    if (item.id == null || item.description == null || item.expirationDate == null || item.minimumAmount == 0) {
                        return@mapNotNull null
                    }
                    FreeShippingCoupon(
                        couponId = item.id,
                        description = item.description,
                        expirationDate = LocalDate.parse(item.expirationDate),
                        minimumOrderPrice = item.minimumAmount
                    )
                }

                "percentage" -> {
                    if (
                        item.id == null ||
                        item.description == null ||
                        item.expirationDate == null ||
                        item.discount == 0 ||
                        item.availableTime?.start == null ||
                        item.availableTime.end == null
                    ) {
                        return@mapNotNull null
                    }
                    MiracleSaleCoupon(
                        couponId = item.id,
                        description = item.description,
                        expirationDate = LocalDate.parse(item.expirationDate),
                        startHour = LocalTime.parse(item.availableTime.start),
                        endHour = LocalTime.parse(item.availableTime.end),
                        discountRate = item.discount.toDouble()
                    )
                }

                else -> null
            }
        }
    }

}