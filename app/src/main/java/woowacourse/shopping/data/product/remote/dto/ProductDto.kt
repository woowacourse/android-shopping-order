package woowacourse.shopping.data.product.remote.dto

import woowacourse.shopping.domain.model.Product

data class ProductDto(
    val id: Long,
    val name: String,
    val price: Int,
    val imageUrl: String,
    val category: String = "",
) {
    companion object {
        fun ProductDto.toDomain(quantity: Int = 0) =
            Product(
                id = id,
                imgUrl = imageUrl,
                name = name,
                price = price,
                quantity = quantity,
            )
    }
}
