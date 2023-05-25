package woowacourse.shopping.domain

data class CartProduct(
    val id: Int,
    val quantity: Int = 0,
    val isChecked: Boolean,
    val product: Product
) {
    fun decreaseAmount(): CartProduct = copy(quantity = quantity - 1)

    fun increaseAmount(): CartProduct = copy(quantity = quantity + 1)

    fun changeChecked(isChecked: Boolean): CartProduct = copy(isChecked = isChecked)
}
