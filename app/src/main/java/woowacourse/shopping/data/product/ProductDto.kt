package woowacourse.shopping.data.product

import woowacourse.shopping.domain.Product

data class ProductDto(
    val id: Long,
    val name: String,
    val price: Int,
    val imageUrl: String
) {
    fun toDomain(): Product {
        return Product(id, imageUrl, name, price)
    }
}
