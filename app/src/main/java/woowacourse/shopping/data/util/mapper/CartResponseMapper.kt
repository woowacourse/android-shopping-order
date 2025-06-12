package woowacourse.shopping.data.util.mapper

import woowacourse.shopping.data.carts.dto.CartResponse
import woowacourse.shopping.data.payment.dto.CouponListResponse
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.coupon.CalculateBonusGoods
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.DiscountedAmount
import woowacourse.shopping.domain.model.coupon.ExpirationPeriod
import woowacourse.shopping.domain.model.coupon.MinimumAmount
import java.time.LocalDate
import java.time.LocalTime

fun CartResponse.toCartItems(): List<CartItem> = this.content.map { CartItem(it.product.toDomain(), it.quantity, it.id) }

fun CouponListResponse.toCouponItems(): List<Coupon> =
    this.mapNotNull { couponItem ->

        val id = couponItem.id
        val code = couponItem.code
        val description = couponItem.description
        val expDate = LocalDate.parse(couponItem.expirationDate)

        when (couponItem.discountType) {
            "fixed" ->
                Coupon.Fixed(
                    id = id,
                    code = code,
                    description = description,
                    expirationPeriod = ExpirationPeriod(endDate = expDate),
                    minimumAmount = MinimumAmount(couponItem.minimumAmount ?: 0),
                    discountedAmount = DiscountedAmount(discountPrice = couponItem.discount ?: 0),
                )

            "buyXgetY" ->
                Coupon.BonusGoods(
                    id = id,
                    code = code,
                    description = description,
                    expirationPeriod = ExpirationPeriod(endDate = expDate),
                    minimumAmount = MinimumAmount(0),
                    calculateBonusGoods =
                        CalculateBonusGoods(
                            buyQuantity = couponItem.buyQuantity ?: 0,
                            getQuantity = couponItem.getQuantity ?: 0,
                        ),
                )

            "freeShipping" ->
                Coupon.FreeShipping(
                    id = id,
                    code = code,
                    description = description,
                    expirationPeriod = ExpirationPeriod(endDate = expDate),
                    minimumAmount = MinimumAmount(couponItem.minimumAmount ?: 0),
                )

            "percentage" ->
                Coupon.Percentage(
                    id = id,
                    code = code,
                    description = description,
                    expirationPeriod =
                        ExpirationPeriod(
                            endDate = expDate,
                            startTime = LocalTime.parse(couponItem.availableTime?.start ?: "00:00:00"),
                            endTime = LocalTime.parse(couponItem.availableTime?.end ?: "23:59:59"),
                        ),
                    minimumAmount = MinimumAmount(0),
                    discountedAmount = DiscountedAmount(discountRate = couponItem.discount ?: 0),
                )

            else -> null
        }
    }
