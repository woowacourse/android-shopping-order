package woowacourse.shopping.data.repository

import woowacourse.shopping.data.model.coupon.BuyXGetYCouponResponse
import woowacourse.shopping.data.model.coupon.CouponResponse
import woowacourse.shopping.data.model.coupon.FixedCouponResponse
import woowacourse.shopping.data.model.coupon.FreeShippingCouponResponse
import woowacourse.shopping.data.model.coupon.PercentageCouponResponse
import woowacourse.shopping.data.source.remote.coupon.CouponRemoteDataSource
import woowacourse.shopping.domain.model.coupon.AvailableTime
import woowacourse.shopping.domain.model.coupon.BuyXGetYCoupon
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.FixedCoupon
import woowacourse.shopping.domain.model.coupon.FreeShippingCoupon
import woowacourse.shopping.domain.model.coupon.PercentageCoupon
import woowacourse.shopping.domain.repository.CouponRepository
import java.time.LocalDate

class CouponRepositoryImpl(
    private val couponRemoteDataSource: CouponRemoteDataSource
) : CouponRepository {
    override suspend fun getCoupons(): Result<List<Coupon>> {
        return couponRemoteDataSource.getCoupons()
            .mapCatching { response ->
                response
                    .map { it.toDomain() }
                    .filter { it.isAvailable() }
            }
    }

    private fun CouponResponse.toDomain(): Coupon {
        return when (this) {
            is BuyXGetYCouponResponse -> this.toDomain()
            is FixedCouponResponse -> this.toDomain()
            is FreeShippingCouponResponse -> this.toDomain()
            is PercentageCouponResponse -> this.toDomain()
        }
    }

    private fun BuyXGetYCouponResponse.toDomain() = BuyXGetYCoupon(
        code = this.code,
        id = this.id,
        description = this.description,
        expirationDate = LocalDate.parse(this.expirationDate),
        buyQuantity = this.buyQuantity,
        getQuantity = this.getQuantity,
        discountType = this.discountType,
    )

    private fun FixedCouponResponse.toDomain() = FixedCoupon(
        code = this.code,
        id = this.id,
        description = this.description,
        expirationDate = LocalDate.parse(this.expirationDate),
        discount = this.discount,
        minimumAmount = this.minimumAmount,
        discountType = this.discountType,
    )

    private fun FreeShippingCouponResponse.toDomain() = FreeShippingCoupon(
        code = this.code,
        id = this.id,
        description = this.description,
        expirationDate = LocalDate.parse(this.expirationDate),
        minimumAmount = this.minimumAmount,
        discountType = this.discountType,
    )

    private fun PercentageCouponResponse.toDomain() = PercentageCoupon(
        code = this.code,
        id = this.id,
        description = this.description,
        expirationDate = LocalDate.parse(this.expirationDate),
        discount = this.discount,
        availableTime = AvailableTime.of(this.availableTime.start, this.availableTime.end),
        discountType = this.discountType,
    )
}