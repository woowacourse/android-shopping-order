package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartProduct

sealed interface Coupon {
    val id: Int
    val code: String
    val description: String
    val expirationDate: String

    fun isValid(items: List<CartProduct>): Boolean
}
