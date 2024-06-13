package woowacourse.shopping.presentation.ui.model

data class ProductModel(
    val id: Long,
    val name: String,
    val imageUrl: String,
    val price: Long,
    val category: String,
    val quantity: Int,
) {
    val calculatedPrice: Int
        get() = (price * quantity).toInt()

    companion object {
        val INVALID_PRODUCT_MODEL = ProductModel(id = -1L, "", "", 0L, "", 0)
    }
}
