package woowacourse.shopping.domain.model

data class CartItem(
    val id: Int,
    val quantity: Int,
) {
    companion object {
        fun getDefault() = CartItem(0, 0)
    }
}
