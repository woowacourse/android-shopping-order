package woowacourse.shopping.remote.dto.request

data class CartSaveRequest(
    val productId: Long,
    val quantity: Int,
)

data class CartUpdateRequest(
    val quantity: Int,
)
