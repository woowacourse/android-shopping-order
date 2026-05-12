package woowacourse.shopping.model

data class User(
    val email: String,
    val password: String,
    val cart: Cart
)
