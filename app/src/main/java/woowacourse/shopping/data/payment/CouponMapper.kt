package woowacourse.shopping.data.payment

import woowacourse.shopping.data.payment.model.AvailableTime
import woowacourse.shopping.data.payment.model.CouponData
import woowacourse.shopping.remote.dto.response.CouponResponse

fun CouponResponse.toData(): CouponData {
    when (this.code) {
        "FIXED5000" -> return CouponData.Fixed5000(
            id,
            code,
            description,
            expirationDate,
            discount,
            minimumAmount,
            discountType,
        )

        "BOGO" -> return CouponData.Bogo(
            id,
            code,
            description,
            expirationDate,
            buyQuantity,
            getQuantity,
            discountType,
        )

        "FREESHIPPING" -> return CouponData.Freeshipping(
            id,
            code,
            description,
            expirationDate,
            minimumAmount,
            discountType,
        )

        "MIRACLESALE" -> return CouponData.Miraclesale(
            id,
            code,
            description,
            expirationDate,
            discount,
            AvailableTime(availableTime.start, availableTime.end),
            discountType,
        )

        else -> throw Exception()
    }
}
