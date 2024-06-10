package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.CartProduct

class Unknown(override val id: Int, override val code: String, override val description: String, override val expirationDate: String, override val discountType: String) : Coupon(
    id,
    code,
    description,
    discountType,
    expirationDate,
) {
    override fun isValid(cartProducts: List<CartProduct>): Boolean {
        return false
    }

    override fun getPriceDiscount(cartProducts: List<CartProduct>): Int {
        return 0
    }

    override fun getDeliveryDiscount(cartProducts: List<CartProduct>): Int {
        return 0
    }
}
