package woowacourse.shopping.domain.model

@JvmInline
value class Coupons(
    val value: List<Coupon>,
) {
    fun toggleCoupon(couponId: Int): Coupons {
        val selectedCoupon = value.find { it.id == couponId } ?: return this

        return Coupons(
            value.map {
                when {
                    it.id == couponId -> it.copy(isSelected = !selectedCoupon.isSelected)
                    else -> it.copy(isSelected = false)
                }
            },
        )
    }

    companion object {
        val EMPTY_COUPONS: Coupons = Coupons(emptyList())
    }
}
