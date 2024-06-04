package woowacourse.shopping.data.datasource.remote.model.request

data class CreateOrderRequest(
    val cartItemIds: List<Int>,
)
