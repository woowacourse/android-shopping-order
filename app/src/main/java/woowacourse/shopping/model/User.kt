package woowacourse.shopping.model

import woowacourse.shopping.model.cart.Cart

data class User(
    val email: String,
    val password: String,
    val cart: Cart,
)
