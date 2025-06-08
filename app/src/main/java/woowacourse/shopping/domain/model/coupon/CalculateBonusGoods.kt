package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartItem

class CalculateBonusGoods(
    val buyQuantity: Int = 0,
    val getQuantity: Int = 0,
) {
    fun getDiscountedPrice(cartItems: List<CartItem>): DiscountedAmount {
        val bonusPrice =
            cartItems
                .filter { it.quantity >= (buyQuantity + getQuantity) }
                .takeIf { it.isNotEmpty() }
                ?.maxBy {
                    it.goods.price
                }?.goods
                ?.price ?: 0
        return DiscountedAmount(discountPrice = bonusPrice * getQuantity)
    }
}
