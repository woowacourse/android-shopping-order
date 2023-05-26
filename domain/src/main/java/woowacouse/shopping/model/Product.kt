package woowacouse.shopping.model

data class Product(
    val id: Long,
    val title: String,
    val price: Int,
    var count: Int = 0,
)
