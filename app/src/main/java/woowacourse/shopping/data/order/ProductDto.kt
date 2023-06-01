package woowacourse.shopping.data.order

import woowacourse.shopping.domain.Product

data class ProductDto(
    val id: Long,
    val name: String,
    val price: Int,
    val imageUrl: String
) {
    fun toDomain(): Product = Product(
        id = id,
        name = name,
        price = price,
        imageUrl = imageUrl
    )
}
