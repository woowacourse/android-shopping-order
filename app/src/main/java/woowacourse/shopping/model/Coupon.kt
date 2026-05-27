package woowacourse.shopping.model

data class Coupon(
    val id: Long,
    val code: String,
    val description: String,
    val expirationDate: String,
    val discount: Int,
    val minimumAmount: Long?,
    val availableTime: AvailableTime?,
    val buyQuantity: Int?,
    val getQuantity: Int?,
    val discountType: DiscountType,
) {
    fun calculateDiscountPrice(
        orderPrice: Long,
        deliveryFee: Long = 0L,
        orderItems: List<CouponOrderItem> = emptyList(),
    ): Long {
        if (isAvailable(orderPrice, orderItems).not()) return 0L

        val discountPrice =
            when (discountType) {
                DiscountType.FIXED -> discount.toLong()
                DiscountType.PERCENTAGE -> orderPrice * discount / 100
                DiscountType.BUY_X_GET_Y -> calculateBuyXGetYDiscount(orderItems)
                DiscountType.FREE_SHIPPING -> deliveryFee
            }

        return when (discountType) {
            DiscountType.FREE_SHIPPING -> discountPrice.coerceAtMost(deliveryFee)
            else -> discountPrice.coerceAtMost(orderPrice)
        }
    }

    fun isAvailable(
        orderPrice: Long,
        orderItems: List<CouponOrderItem>,
    ): Boolean {
        val isDiscountSatisfied = minimumAmount == null || orderPrice >= minimumAmount
        val isQuantitySatisfied =
            if (discountType == DiscountType.BUY_X_GET_Y) {
                val buy = buyQuantity ?: return false
                val get = getQuantity ?: return false
                buy > 0 && get > 0 && orderItems.any { it.quantity >= buy + get }
            } else {
                true
            }

        return isDiscountSatisfied && isQuantitySatisfied
    }

    private fun calculateBuyXGetYDiscount(orderItems: List<CouponOrderItem>): Long {
        val buy = buyQuantity ?: return 0L
        val get = getQuantity ?: return 0L
        val count = buy + get
        if (buy <= 0 || get <= 0 || count <= 0) return 0L

        val expensiveItem =
            orderItems
                .filter { orderItem -> orderItem.quantity >= count }
                .maxByOrNull { orderItem -> orderItem.totalPrice / orderItem.quantity }
                ?: return 0L

        val price = expensiveItem.totalPrice / expensiveItem.quantity
        val freeQuantity = (expensiveItem.quantity / count * get).coerceAtMost(expensiveItem.quantity)

        return price * freeQuantity
    }
}

data class CouponOrderItem(
    val cartItemId: Long,
    val totalPrice: Long,
    val quantity: Int,
)

data class AvailableTime(
    val start: String,
    val end: String,
)

enum class DiscountType {
    FIXED,
    PERCENTAGE,
    BUY_X_GET_Y,
    FREE_SHIPPING,
}
