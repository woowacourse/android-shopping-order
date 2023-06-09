package woowacourse.shopping.data.dto

typealias OrderedProductDto = OrderedProduct

class OrderedProduct(
    val name: String,
    val price: Int,
    val quantity: Int,
    val imageUrl: String,
)
