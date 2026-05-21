package woowacourse.shopping.data.model

data class User(
    val email: String,
    val password: String,
    val cart: Cart,
)
