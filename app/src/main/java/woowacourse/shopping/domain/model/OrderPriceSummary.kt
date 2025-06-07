package woowacourse.shopping.domain.model

data class OrderPriceSummary(
    val productTotalPrice: Int,
    val discountAmount: Int = 0,
    val deliveryFee: Int = 3_000,
    val cartItems: List<CartItem>,
) {
    val finalPrice: Int
        get() = productTotalPrice - discountAmount + deliveryFee

    fun applyCoupon(coupon: Coupon): OrderPriceSummary =
        when (val type = coupon.discountType) {
            is DiscountType.FixedAmount -> {
                this.copy(discountAmount = type.amount)
            }

            is DiscountType.Percentage -> {
                val discount = (productTotalPrice * type.rate * 0.01).toInt()
                this.copy(discountAmount = discount)
            }

            is DiscountType.FreeShipping -> {
                this.copy(deliveryFee = 0)
            }

            is DiscountType.BuyXGetY -> {
                val bogoDiscount = calculateBogoDiscount(type.buyQuantity, type.getQuantity)
                this.copy(discountAmount = bogoDiscount)
            }
        }

    fun removeCoupon(): OrderPriceSummary =
        this.copy(
            discountAmount = 0,
            deliveryFee = 3_000,
        )

    private fun calculateBogoDiscount(
        buyQuantity: Int,
        getQuantity: Int,
    ): Int {
        val eligibleItems = cartItems.filter { it.quantity >= buyQuantity + getQuantity }
        if (eligibleItems.isEmpty()) return 0

        val maxPricedItem = eligibleItems.maxByOrNull { it.product.price } ?: return 0

        return maxPricedItem.product.price * getQuantity
    }
}
