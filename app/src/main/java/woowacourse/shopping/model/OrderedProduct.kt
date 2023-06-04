package woowacourse.shopping.model

typealias UiOrderedProduct = OrderedProduct

class OrderedProduct(
    val name: String,
    val price: Int,
    val quantity: Int,
    val imageUrl: String,
)
