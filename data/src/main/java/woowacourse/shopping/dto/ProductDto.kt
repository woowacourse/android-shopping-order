package woowacourse.shopping.dto

import woowacourse.shopping.model.Product

class ProductDto(
    var id: Int = 0,
    var name: String = "",
    var price: Int = 0,
    var imageUrl: String = ""
) {
    fun toDomain(): Product = Product(
        id = id,
        name = name,
        price = price,
        imageUrl = imageUrl
    )
}
