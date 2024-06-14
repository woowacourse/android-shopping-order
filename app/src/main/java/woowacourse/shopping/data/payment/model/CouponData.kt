package woowacourse.shopping.data.payment.model

sealed class CouponData {
    abstract val id: Long
    abstract val code: String
    abstract val description: String
    abstract val expirationDate: String
    abstract val discountType: String

    data class Fixed5000(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: String,
        val discount: Int,
        val minimumAmount: Int,
        override val discountType: String = "fixed",
    ) : CouponData()

    data class Bogo(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: String,
        val buyQuantity: Int,
        val getQuantity: Int,
        override val discountType: String = "buyXgetY",
    ) : CouponData()

    data class Freeshipping(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: String,
        val minimumAmount: Int,
        override val discountType: String = "freeShipping",
    ) : CouponData()

    data class Miraclesale(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: String,
        val discount: Int,
        val availableTime: AvailableTime,
        override val discountType: String = "percentage",
    ) : CouponData()
}

data class AvailableTime(
    val start: String,
    val end: String,
)
