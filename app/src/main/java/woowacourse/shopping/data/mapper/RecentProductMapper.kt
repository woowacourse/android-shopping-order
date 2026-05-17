package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.local.recent.RecentProductEntity
import woowacourse.shopping.domain.model.product.Category
import woowacourse.shopping.domain.model.product.ImageUrl
import woowacourse.shopping.domain.model.product.Price
import woowacourse.shopping.domain.model.product.Product
import woowacourse.shopping.domain.model.product.ProductName

fun RecentProductEntity.toDomain(): Product =
    Product(
        id = productId,
        imageUrl = ImageUrl(imageUrl),
        name = ProductName(name),
        price = Price(price),
        category = Category(category),
    )

fun Product.toRecentProductEntity(viewedAt: Long): RecentProductEntity =
    RecentProductEntity(
        productId = id,
        name = name.value,
        price = price.value,
        imageUrl = imageUrl.value,
        category = category.value,
        viewedAt = viewedAt,
    )
