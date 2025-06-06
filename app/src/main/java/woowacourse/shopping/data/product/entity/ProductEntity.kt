package woowacourse.shopping.data.product.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import woowacourse.shopping.domain.product.Product

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val price: Int,
    val category: String,
    val imageUrl: String? = null,
) {
    fun toDomain(): Product = Product(id, name, price, category, imageUrl)

    companion object {
        fun Product.toEntity(): ProductEntity = ProductEntity(id, name, price, category, imageUrl)
    }
}
