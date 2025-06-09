package woowacourse.shopping.data.coupon.repository

import woowacourse.shopping.data.coupon.dataSource.CouponRemoteDataSource
import woowacourse.shopping.domain.order.Coupon
import woowacourse.shopping.domain.order.DiscountType
import java.time.LocalDate
import java.time.LocalTime

class DefaultCouponRepository(
    private val couponRemoteDataSource: CouponRemoteDataSource,
) : CouponRepository {
    override suspend fun getCoupons(): Result<List<Coupon>> =
        runCatching {
            couponRemoteDataSource.getCoupons().map {
                when (DiscountType.from(it.discountType)) {
                    DiscountType.PRICE_DISCOUNT ->
                        Coupon.PriceDiscount(
                            id = it.id,
                            code = it.code,
                            description = it.description,
                            expirationDate = LocalDate.parse(it.expirationDate),
                            discount = it.discount ?: throw IllegalArgumentException(""),
                            minimumAmount = it.minimumAmount ?: throw IllegalArgumentException(""),
                        )

                    DiscountType.BONUS ->
                        Coupon.Bonus(
                            id = it.id,
                            code = it.code,
                            description = it.description,
                            expirationDate = LocalDate.parse(it.expirationDate),
                            buyQuantity = it.buyQuantity,
                            getQuantity = it.getQuantity,
                        )

                    DiscountType.FREE_SHIPPING ->
                        Coupon.FreeShipping(
                            id = it.id,
                            code = it.code,
                            description = it.description,
                            expirationDate = LocalDate.parse(it.expirationDate),
                            minimumAmount = it.minimumAmount ?: throw IllegalArgumentException(""),
                        )

                    DiscountType.PERCENTAGE_DISCOUNT ->
                        Coupon.PercentageDiscount(
                            id = it.id,
                            code = it.code,
                            description = it.description,
                            expirationDate = LocalDate.parse(it.expirationDate),
                            discount = it.discount ?: throw IllegalArgumentException(""),
                            availableStartTime = LocalTime.parse(it.availableTime?.start),
                            availableEndTime = LocalTime.parse(it.availableTime?.end),
                        )
                }
            }
        }

    companion object {
        private var instance: CouponRepository? = null

        fun initialize(couponRemoteDataSource: CouponRemoteDataSource) {
            if (instance == null) {
                instance =
                    DefaultCouponRepository(couponRemoteDataSource = couponRemoteDataSource)
            }
        }

        fun get(): CouponRepository = instance ?: throw IllegalStateException("초기화 되지 않았습니다.")
    }
}
