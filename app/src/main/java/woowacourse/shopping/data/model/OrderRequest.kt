package woowacourse.shopping.data.model

data class OrderRequest(
    val basketIds: List<Long>,
    val usingPoint: Long,
    val totalPrice: Long,
)
