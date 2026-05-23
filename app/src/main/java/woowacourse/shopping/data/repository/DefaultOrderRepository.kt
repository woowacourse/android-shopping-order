package woowacourse.shopping.data.repository

import woowacourse.shopping.data.source.remote.api.OrderItemsRequest
import woowacourse.shopping.data.source.remote.api.OrderService
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
import woowacourse.shopping.error.Error
import woowacourse.shopping.error.NetworkError
import woowacourse.shopping.error.Result
import java.time.LocalDate

class DefaultOrderRepository(
    private val remoteOrderDataSource: OrderService,
) : OrderRepository {
    override suspend fun getCoupons(): Result<List<Coupon>, NetworkError> =
        safeNetworkApiCall {
            remoteOrderDataSource.getCoupons().map { couponResponse ->
                couponResponse.toCoupon()
            }
        }

    override suspend fun orderCartItems(cartItemIds: List<Long>): Result<Unit, Error> =
        safeNetworkApiCall {
            remoteOrderDataSource.order(cartItemIds = OrderItemsRequest(cartItemIds))
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
