package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.Cart

sealed interface CouponCondition {
    fun isValid(carts: List<Cart>): Boolean
}
