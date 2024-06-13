package woowacourse.shopping.remote.dto.response

import woowacourse.shopping.domain.model.Product

data class ProductDto(
    val id: Int,
    val name: String,
    val price: Int,
    val imageUrl: String,
    val category: String,
) {
    fun toProduct(): Product {
        return Product(
            id = id.toLong(),
            name = name,
            imgUrl = imageUrl,
            price = price.toLong(),
            category = category,
        )
    }
}
