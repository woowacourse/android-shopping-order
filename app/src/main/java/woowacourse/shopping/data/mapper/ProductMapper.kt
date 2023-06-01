package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.ProductEntity
import woowacouse.shopping.model.product.Product

fun ProductEntity.toModel(): Product = Product(id, name, price, imageUrl)
