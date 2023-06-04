package woowacourse.shopping.domain

data class OrderHistory(
    val id: Int,
    val price: Int,
    val quantity: Int,
    val name: String
)