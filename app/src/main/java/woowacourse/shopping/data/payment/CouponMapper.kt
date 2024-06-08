package woowacourse.shopping.data.payment

import woowacourse.shopping.data.payment.model.AvailableTime
import woowacourse.shopping.data.payment.model.CouponData
import woowacourse.shopping.data.payment.model.CouponsData
import woowacourse.shopping.remote.dto.response.CouponResponse
import woowacourse.shopping.remote.dto.response.CouponsResponse

fun CouponResponse.toData(): CouponData {
    when (this) {
        is CouponResponse.Fixed5000 -> return CouponData.Fixed5000(
            id,
            code,
            description,
            expirationDate,
            discount,
            minimumAmount,
            discountType,
        )

        is CouponResponse.Bogo -> return CouponData.Bogo(
            id,
            code,
            description,
            expirationDate,
            buyQuantity,
            getQuantity,
            discountType,
        )

        is CouponResponse.Freeshipping -> return CouponData.Freeshipping(
            id,
            code,
            description,
            expirationDate,
            minimumAmount,
            discountType,
        )

        is CouponResponse.Miraclesale -> return CouponData.Miraclesale(
            id,
            code,
            description,
            expirationDate,
            discount,
            AvailableTime(availableTime.start, availableTime.end),
            discountType,
        )
    }
}

fun CouponsResponse.toData(): CouponsData {
    return CouponsData(coupons = coupons.map { it.toData() })
}
