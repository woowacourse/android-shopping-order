package woowacourse.shopping.data.datasource.local.room.entity.product

import woowacourse.shopping.domain.model.Product

fun ProductEntity.toProduct() = Product(id, name, price, imageUrl, category)

fun List<ProductEntity>.toProducts(): List<Product> = map { it.toProduct() }

fun Product.toProductEntity() = ProductEntity(id, name, price, imageUrl, category)

fun List<Product>.toProductEntities(): List<ProductEntity> = map { it.toProductEntity() }
