package woowacourse.shopping.domain

enum class DiscountType(val typeName: String) {
    FreeShipping("freeShipping"),
    Fixed("fixed"),
    BuyXGetY("buyXgetY"),
    Percentage("percentage"),
    ;

    companion object {
        fun from(discountType: String): DiscountType {
            return when (discountType) {
                FreeShipping.typeName -> {
                    FreeShipping
                }

                Fixed.typeName -> {
                    Fixed
                }

                BuyXGetY.typeName -> {
                    BuyXGetY
                }

                Percentage.typeName -> {
                    Percentage
                }

                else -> {
                    throw IllegalArgumentException("잘못된 쿠폰 타입입니다.")
                }
            }
        }
    }
}
