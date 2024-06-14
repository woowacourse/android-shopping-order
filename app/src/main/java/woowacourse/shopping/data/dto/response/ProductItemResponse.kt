package woowacourse.shopping.data.dto.response

import woowacourse.shopping.domain.Product

data class ProductItemResponse(
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
