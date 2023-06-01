package woowacourse.shopping.model

data class CartProductModel(
    val isChecked: Boolean,
    val cartId: Int,
    val id: Int,
    val name: String,
    val imageUrl: String,
    var quantity: Int,
    val price: Int,
)
