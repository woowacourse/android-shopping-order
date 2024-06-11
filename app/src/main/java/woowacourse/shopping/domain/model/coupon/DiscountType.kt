package woowacourse.shopping.domain.model.coupon

enum class DiscountType {
    FIXED,
    BUYXGETY,
    FREESHIPPING,
    PERCENTAGE, ;

    companion object {
        fun getDiscountType(discountType: String): DiscountType {
            return DiscountType.entries.find { it.name == discountType.uppercase() }
                ?: throw IllegalArgumentException()
        }
    }
}
