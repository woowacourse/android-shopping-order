package woowacourse.shopping.data.model

typealias DataOrderedProduct = OrderedProduct

class OrderedProduct(
    val name: String,
    val price: Int,
    val quantity: Int,
    val imageUrl: String,
)
