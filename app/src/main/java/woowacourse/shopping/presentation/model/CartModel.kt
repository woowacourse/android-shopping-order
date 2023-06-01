package woowacourse.shopping.presentation.model

data class CartModel(
    val id: Long,
    val product: ProductModel,
    val count: Int,
    var checked: Boolean,
) {
    fun updateId(id: Long): CartModel {
        return copy(id = id)
    }
}
