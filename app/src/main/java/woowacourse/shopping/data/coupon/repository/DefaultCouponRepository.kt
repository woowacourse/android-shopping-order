package woowacourse.shopping.data.coupon.repository

import woowacourse.shopping.data.coupon.dataSource.CouponRemoteDataSource
import woowacourse.shopping.data.coupon.remote.dto.BuyXGetYCoupon
import woowacourse.shopping.data.coupon.remote.dto.FixedCoupon
import woowacourse.shopping.data.coupon.remote.dto.FreeShippingCoupon
import woowacourse.shopping.data.coupon.remote.dto.PercentageCoupon
import woowacourse.shopping.domain.order.Coupon
import java.time.LocalDate
import java.time.LocalTime

class DefaultCouponRepository(
    private val couponRemoteDataSource: CouponRemoteDataSource,
) : CouponRepository {
    override suspend fun getCoupons(): Result<List<Coupon>> =
        runCatching {
            couponRemoteDataSource.getCoupons().map { dto ->
                when (dto) {
                    is FixedCoupon ->
                        Coupon.PriceDiscount(
                            id = dto.id,
                            code = dto.code,
                            description = dto.description,
                            expirationDate = LocalDate.parse(dto.expirationDate),
                            discount = dto.discount,
                            minimumAmount = dto.minimumAmount,
                        )

                    is BuyXGetYCoupon ->
                        Coupon.Bonus(
                            id = dto.id,
                            code = dto.code,
                            description = dto.description,
                            expirationDate = LocalDate.parse(dto.expirationDate),
                            buyQuantity = dto.buyQuantity,
                            getQuantity = dto.getQuantity,
                        )

                    is FreeShippingCoupon ->
                        Coupon.FreeShipping(
                            id = dto.id,
                            code = dto.code,
                            description = dto.description,
                            expirationDate = LocalDate.parse(dto.expirationDate),
                            minimumAmount = dto.minimumAmount,
                        )

                    is PercentageCoupon ->
                        Coupon.PercentageDiscount(
                            id = dto.id,
                            code = dto.code,
                            description = dto.description,
                            expirationDate = LocalDate.parse(dto.expirationDate),
                            discountPercentage = dto.discount,
                            availableStartTime =
                                dto.availableTime.start.let(LocalTime::parse)
                                    ?: throw IllegalArgumentException("사용 가능한 시작 시간이 없습니다."),
                            availableEndTime =
                                dto.availableTime.end.let(LocalTime::parse)
                                    ?: throw IllegalArgumentException("사용 가능한 종료 시간이 없습니다."),
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
