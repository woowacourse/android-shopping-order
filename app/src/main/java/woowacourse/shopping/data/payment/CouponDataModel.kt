package woowacourse.shopping.data.payment

import woowacourse.shopping.data.payment.CouponResponse.Coupon

class CouponDataModel(
    val id: Long,
    val code: String,
    val discountType: String,
    val description: String,
    val expirationDate: String,
    val discount: Int?,
    val minimumAmount: Int?,
    val buyQuantity: Int?,
    val getQuantity: Int?,
    val availableTime: AvailableTime?,
) {
    class AvailableTime(
        val start: String,
        val end: String,
    )

    companion object {
        fun Coupon.toDataModel(): CouponDataModel? {
            return CouponDataModel(
                id ?: return null,
                code ?: return null,
                discountType ?: return null,
                description ?: return null,
                expirationDate ?: return null,
                discount,
                minimumAmount,
                buyQuantity,
                getQuantity,
                AvailableTime(
                    availableTime?.start ?: return null,
                    availableTime.end ?: return null,
                ),
            )
        }
    }
}
