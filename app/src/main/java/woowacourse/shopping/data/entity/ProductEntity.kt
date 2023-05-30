package woowacourse.shopping.data.entity

import com.squareup.moshi.JsonClass
import woowacourse.shopping.domain.product.Product

@JsonClass(generateAdapter = true)
data class ProductEntity(
    val id: Long,
    val name: String,
    val price: Int,
    val imageUrl: String
) {
    companion object {
        fun Product.toEntity() = ProductEntity(
            id, name, price, imageUrl
        )

        fun ProductEntity.toDomain() = Product(
            id, name, price, imageUrl
        )
    }
}
