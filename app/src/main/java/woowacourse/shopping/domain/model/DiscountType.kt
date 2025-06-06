package woowacourse.shopping.domain.model

sealed class DiscountType {
    data class FixedAmount(
        val amount: Int,
    ) : DiscountType()

    data class Percentage(
        val rate: Int,
    ) : DiscountType()

    data object FreeShipping : DiscountType()

    data class BuyXGetY(
        val buyQuantity: Int,
        val getQuantity: Int,
    ) : DiscountType()
}
