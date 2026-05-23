package woowacourse.shopping.data.repository

import woowacourse.shopping.data.source.remote.api.CouponService
import woowacourse.shopping.data.source.remote.api.safeNetworkApiCall
import woowacourse.shopping.data.source.remote.dto.coupon.response.BuyXgetYCouponResponse
import woowacourse.shopping.data.source.remote.dto.coupon.response.CouponResponse
import woowacourse.shopping.data.source.remote.dto.coupon.response.FixedCouponResponse
import woowacourse.shopping.data.source.remote.dto.coupon.response.FreeShippingCouponResponse
import woowacourse.shopping.data.source.remote.dto.coupon.response.PercentageCouponResponse
import woowacourse.shopping.domain.model.payment.BuyXGetYCoupon
import woowacourse.shopping.domain.model.payment.Coupon
import woowacourse.shopping.domain.model.payment.FixedAmountCoupon
import woowacourse.shopping.domain.model.payment.FreeShippingCoupon
import woowacourse.shopping.domain.model.payment.PercentageCoupon
import woowacourse.shopping.error.NetworkError
import woowacourse.shopping.error.Result
import java.time.LocalDate

class DefaultPaymentRepository(
    val remoteCouponDataSource: CouponService,
) : PaymentRepository {
    override suspend fun getCoupons(): Result<List<Coupon>, NetworkError> =
        safeNetworkApiCall {
            remoteCouponDataSource.getCoupons().map { couponResponse ->
                couponResponse.toCoupon()
            }
        }
}

private fun CouponResponse.toCoupon(): Coupon =
    when (this) {
        is BuyXgetYCouponResponse -> {
            BuyXGetYCoupon(
                code = this.code,
                expirationDate = LocalDate.parse(this.expirationDate),
                buyQuantity = this.buyQuantity,
                freeGetQuantity = this.getQuantity,
            )
        }

        is FixedCouponResponse -> {
            FixedAmountCoupon(
                code = this.code,
                expirationDate = LocalDate.parse(this.expirationDate),
                minimumAmount = this.minimumAmount,
                discountAmount = this.discount,
            )
        }

        is FreeShippingCouponResponse -> {
            FreeShippingCoupon(
                code = this.code,
                expirationDate = LocalDate.parse(this.expirationDate),
                minimumAmount = this.minimumAmount,
            )
        }

        is PercentageCouponResponse -> {
            PercentageCoupon(
                code = this.code,
                expirationDate = LocalDate.parse(this.expirationDate),
                discountRate = this.discount,
            )
        }
    }
