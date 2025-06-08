package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.coupon.DiscountType.entries

enum class DiscountType(
    private val title: String,
) {
    PRICE_DISCOUNT("fixed"),
    BONUS("buyXgetY"),
    FREE_SHIPPING("freeShipping"),
    PERCENTAGE_DISCOUNT("percentage"),
    ;

    companion object {
        fun from(title: String): DiscountType =
            entries.find { it.title == title }
                ?: throw IllegalArgumentException("$title 에 해당하는 할인 타입이 없습니다.")
    }
}
