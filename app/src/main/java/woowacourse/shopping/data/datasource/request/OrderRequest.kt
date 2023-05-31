package woowacourse.shopping.data.datasource.request

data class OrderRequest(
    val basketIds: List<Long>,
    val usingPoint: Long,
    val totalPrice: Long,
)
