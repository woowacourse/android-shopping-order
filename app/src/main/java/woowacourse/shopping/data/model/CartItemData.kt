package woowacourse.shopping.data.model

data class CartItemData(
    val id: Long,
    val quantity: Int,
    val product: ProductData,
)
