package woowacourse.shopping.remote.model.response

import woowacourse.shopping.data.model.CouponData

data class CouponResponse(
    val id: Long,
    val code: String,
    val description: String,
    val expirationDate: String,
    val discount: Int? = null,
    val minimumAmount: Int? = null,
    val buyQuantity: Int? = null,
    val getQuantity: Int? = null,
    val availableTime: AvailableTimeResponse? = null,
    val discountType: String,
)

fun CouponResponse.toData(): CouponData = CouponData(
    id = id,
    code = code,
    description = description,
    expirationDate = expirationDate,
    discountType = discountType,
    discount = discount,
    minimumAmount = minimumAmount,
    buyQuantity = buyQuantity,
    getQuantity = getQuantity,
    availableTime = this.availableTime?.toData2(),
)
