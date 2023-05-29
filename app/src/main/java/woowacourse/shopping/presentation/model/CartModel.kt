package woowacourse.shopping.presentation.model

data class CartModel(
    val id: Long,
    val product: ProductModel,
    val count: Int,
    var checked: Boolean,
)
