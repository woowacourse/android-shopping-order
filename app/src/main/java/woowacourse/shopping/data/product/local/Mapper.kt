package woowacourse.shopping.data.product.local

import woowacourse.shopping.data.product.local.entity.ProductEntity
import woowacourse.shopping.domain.model.Product

fun ProductEntity.toProduct() = Product(id, name, price, imageUrl, category)

fun List<ProductEntity>.toProducts(): List<Product> = map { it.toProduct() }

fun Product.toProductEntity() = ProductEntity(id, name, price, imageUrl, category)

fun List<Product>.toProductEntities(): List<ProductEntity> = map { it.toProductEntity() }
