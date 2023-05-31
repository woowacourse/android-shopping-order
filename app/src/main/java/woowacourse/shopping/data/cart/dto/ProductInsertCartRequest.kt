package woowacourse.shopping.data.cart.dto

data class ProductInsertCartRequest(
    val productId: Long,
    val quantity: Int,
)
