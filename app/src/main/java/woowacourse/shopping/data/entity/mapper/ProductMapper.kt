package woowacourse.shopping.data.entity.mapper

import woowacourse.shopping.data.entity.ProductEntity
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.URL

object ProductMapper: Mapper<Product, ProductEntity> {
    override fun Product.toEntity(): ProductEntity {
        return ProductEntity(
            id,
            title,
            price,
            picture.value
        )
    }

    override fun ProductEntity.toDomain(): Product {
        return Product(
            id,
            URL(imageUrl),
            name,
            price
        )
    }
}