package woowacourse.shopping.data.util.mapper

import kotlinx.serialization.json.Json
import woowacourse.shopping.data.carts.dto.CartResponse
import woowacourse.shopping.data.payment.dto.CouponDto
import woowacourse.shopping.data.payment.dto.CouponListResponse
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.coupon.CalculateBonusGoods
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.Coupon.BonusGoods
import woowacourse.shopping.domain.model.coupon.Coupon.Fixed
import woowacourse.shopping.domain.model.coupon.Coupon.FreeShipping
import woowacourse.shopping.domain.model.coupon.Coupon.Percentage
import woowacourse.shopping.domain.model.coupon.DiscountedAmount
import woowacourse.shopping.domain.model.coupon.ExpirationPeriod
import woowacourse.shopping.domain.model.coupon.MinimumAmount
import java.time.LocalDate
import java.time.LocalTime

fun CartResponse.toCartItems(): List<CartItem> = this.content.map { CartItem(it.product.toDomain(), it.quantity, it.id) }

fun CouponListResponse.toCouponItems(): List<Coupon> =
    Json
        .decodeFromString<List<CouponDto>>(Json.encodeToString(this))
        .map { it.toCoupon() }

fun CouponDto.toCoupon(): Coupon {
    val expDate = LocalDate.parse(expirationDate)

    return when (this) {
        is CouponDto.FixedDto ->
            Fixed(
                id = id,
                code = code,
                description = description,
                expirationPeriod = ExpirationPeriod(endDate = expDate),
                minimumAmount = MinimumAmount(minimumAmount ?: 0),
                discountedAmount = DiscountedAmount(discountPrice = discount ?: 0),
            )

        is CouponDto.BonusGoodsDto ->
            BonusGoods(
                id = id,
                code = code,
                description = description,
                expirationPeriod = ExpirationPeriod(endDate = expDate),
                minimumAmount = MinimumAmount(0),
                calculateBonusGoods =
                    CalculateBonusGoods(
                        buyQuantity = buyQuantity ?: 0,
                        getQuantity = getQuantity ?: 0,
                    ),
            )
        is CouponDto.FreeShippingDto ->
            FreeShipping(
                id = id,
                code = code,
                description = description,
                expirationPeriod = ExpirationPeriod(endDate = expDate),
                minimumAmount = MinimumAmount(minimumAmount ?: 0),
            )

        is CouponDto.PercentageDto ->
            Percentage(
                id = id,
                code = code,
                description = description,
                expirationPeriod =
                    ExpirationPeriod(
                        endDate = expDate,
                        startTime =
                            availableTime?.start?.let {
                                LocalTime.parse(it)
                            } ?: LocalTime.MIN,
                        endTime =
                            availableTime?.end?.let {
                                LocalTime.parse(it)
                            } ?: LocalTime.MAX,
                    ),
                minimumAmount = MinimumAmount(0),
                discountedAmount = DiscountedAmount(discountRate = discount ?: 0),
            )
    }
}
