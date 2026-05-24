package woowacourse.shopping.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
import woowacourse.shopping.error.Result
import java.time.LocalDate
import java.time.LocalTime

class DefaultOrderRepository(
    private val remoteOrderDataSource: OrderService,
) : OrderRepository {
    private val _coupons = MutableStateFlow<List<Coupon>>(emptyList())
    override val coupons = _coupons.asStateFlow()

    override suspend fun loadCoupons() {
        val result =
            safeNetworkApiCall {
                remoteOrderDataSource.getCoupons().map { it.toCoupon() }
            }
        if (result is Result.Success) {
            _coupons.value = result.data
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
                availableTimeStart = LocalTime.parse(this.availableTime.start),
                availableTimeEnd = LocalTime.parse(this.availableTime.end),
            )
        }
    }
