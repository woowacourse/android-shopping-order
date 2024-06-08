package woowacourse.shopping.utils.Mapper

import woowacourse.shopping.data.model.RecentlyProductEntity
import woowacourse.shopping.data.remote.dto.product.ProductDto
import woowacourse.shopping.data.remote.dto.product.ProductResponse
import woowacourse.shopping.domain.model.cart.CartItemCounter
import woowacourse.shopping.domain.model.product.Product
import woowacourse.shopping.domain.model.product.RecentlyProduct
import woowacourse.shopping.domain.model.selector.ItemSelector

object ProductMapper {
    fun ProductResponse.toProducts(): List<Product> {
        return productDto.map { it.toProduct() }
    }

    fun ProductDto.toProduct(quantity: Int = 0): Product {
        return Product(
            id = id.toLong(),
            name = name,
            imageUrl = imageUrl,
            price = price,
            category = category,
            cartItemCounter = CartItemCounter(quantity),
            itemSelector = ItemSelector(),
        )
    }

    fun RecentlyProductEntity.toRecentlyProduct(): RecentlyProduct {
        return RecentlyProduct(
            id = id,
            productId = productId,
            imageUrl = imageUrl,
            name = name,
            category = category,
        )
    }

    fun RecentlyProduct.toRecentlyProductEntity(): RecentlyProductEntity {
        return RecentlyProductEntity(
            productId = productId,
            name = name,
            imageUrl = imageUrl,
            category = category,
        )
    }
}
