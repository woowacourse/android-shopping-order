package woowacourse.shopping.data.payment

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
        fun CouponResponse.toDataModel(): CouponDataModel? {
            val availableTime: AvailableTime? =
                if (availableTime == null && availableTime?.start == null && availableTime?.end == null) {
                    null
                } else {
                    AvailableTime(
                        availableTime.start!!,
                        availableTime.end!!,
                    )
                }
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
                availableTime,
            )
        }
    }
}
