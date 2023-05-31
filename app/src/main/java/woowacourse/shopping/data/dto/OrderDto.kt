package woowacourse.shopping.data.dto

data class OrderRequest(
    val orderInfos: List<OrderInfo>,
    val payment: Int,
    val point: Int,
)

data class OrderInfo(
    val productId: Int,
    val quantity: Int,
)
