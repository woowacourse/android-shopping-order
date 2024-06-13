package woowacourse.shopping.data.model.coupon

import com.google.gson.annotations.JsonAdapter
import woowacourse.shopping.data.model.coupon.CouponResponseItem.Companion.parseNullableLocalTime
import woowacourse.shopping.data.remote.CouponResponseItemDeserializer
import woowacourse.shopping.domain.model.Coupon
import java.time.LocalDate
import java.time.LocalTime

@JsonAdapter(CouponResponseItemDeserializer::class)
sealed class CouponResponseItem {
    data class Fixed(
        val id: Int,
        val code: String,
        val description: String,
        val expirationDate: String,
        val discountType: String,
        val discount: Int,
        val minimumAmount: Int,
    ) : CouponResponseItem()

    data class BuyXGetY(
        val id: Int,
        val code: String,
        val description: String,
        val expirationDate: String,
        val discountType: String,
        val buyQuantity: Int,
        val getQuantity: Int,
    ) : CouponResponseItem()

    data class FreeShipping(
        val id: Int,
        val code: String,
        val description: String,
        val expirationDate: String,
        val discountType: String,
        val minimumAmount: Int,
    ) : CouponResponseItem()

    data class MiracleSale(
        val id: Int,
        val code: String,
        val description: String,
        val expirationDate: String,
        val discountType: String,
        val discount: Int,
        val availableTime: AvailableTime,
    ) : CouponResponseItem()

    data class Etc(
        val id: Int,
        val code: String,
        val description: String,
        val discountType: String,
        val expirationDate: String,
        val availableTime: AvailableTime?,
        val buyQuantity: Int?,
        val discount: Int?,
        val getQuantity: Int?,
        val minimumAmount: Int?,
    ) : CouponResponseItem()

    companion object {
        fun parseNullableLocalTime(time: String?): LocalTime? =
            if (time != null) LocalTime.parse(time) else null
    }
}

fun CouponResponseItem.toCoupon(): Coupon {
    return when (this) {
        is CouponResponseItem.Fixed -> toFixedCoupon()
        is CouponResponseItem.BuyXGetY -> toBuyXGetXCoupon()
        is CouponResponseItem.FreeShipping -> toFreeShippingCoupon()
        is CouponResponseItem.MiracleSale -> toMiracleSaleCoupon()
        is CouponResponseItem.Etc -> toEtcCoupon()
    }
}

fun CouponResponseItem.Fixed.toFixedCoupon(): Coupon.Fixed {
    return Coupon.Fixed(
        id = id,
        code = code,
        description = description,
        expirationDate = LocalDate.parse(expirationDate),
        discountType = discountType,
        discount = discount,
        minimumAmount = minimumAmount
    )
}

fun CouponResponseItem.BuyXGetY.toBuyXGetXCoupon(): Coupon.BuyXGetY {
    return Coupon.BuyXGetY(
        id = id,
        code = code,
        description = description,
        expirationDate = LocalDate.parse(expirationDate),
        discountType = discountType,
        buyQuantity = buyQuantity,
        getQuantity = getQuantity
    )
}

fun CouponResponseItem.FreeShipping.toFreeShippingCoupon(): Coupon.FreeShipping {
    return Coupon.FreeShipping(
        id = id,
        code = code,
        description = description,
        expirationDate = LocalDate.parse(expirationDate),
        discountType = discountType,
        minimumAmount = minimumAmount
    )
}

fun CouponResponseItem.MiracleSale.toMiracleSaleCoupon(): Coupon.MiracleSale {
    return Coupon.MiracleSale(
        id = id,
        code = code,
        description = description,
        expirationDate = LocalDate.parse(expirationDate),
        discountType = discountType,
        discount = discount,
        startTime = LocalTime.parse(availableTime.start),
        endTime = LocalTime.parse(availableTime.end),
    )
}

fun CouponResponseItem.Etc.toEtcCoupon(): Coupon.Etc {
    return Coupon.Etc(
        id = id,
        code = code,
        description = description,
        expirationDate = LocalDate.parse(expirationDate),
        discountType = discountType,
        startTime = parseNullableLocalTime(availableTime?.end),
        endTime = parseNullableLocalTime(availableTime?.end),
        buyQuantity = buyQuantity,
        discount = discount,
        getQuantity = getQuantity,
        minimumAmount = minimumAmount,
    )
}
