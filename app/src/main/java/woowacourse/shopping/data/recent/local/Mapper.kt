package woowacourse.shopping.data.recent.local

import woowacourse.shopping.data.recent.local.entity.ProductEntity
import woowacourse.shopping.data.recent.local.entity.RecentProductEntity
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentProduct

fun RecentProductEntity.toRecentProduct() = RecentProduct(id, product.toProduct(), seenDateTime)

fun List<RecentProductEntity>.toRecentProducts() = map { it.toRecentProduct() }

fun ProductEntity.toProduct() = Product(productId, name, price, imageUrl, category)

fun Product.toProductEntity() = ProductEntity(id, name, price, imageUrl, category)
