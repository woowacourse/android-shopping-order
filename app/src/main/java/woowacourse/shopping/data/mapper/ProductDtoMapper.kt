package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.remote.dto.product.ProductDto
import woowacourse.shopping.data.remote.dto.product.ProductResponse
import woowacourse.shopping.domain.model.cart.CartItemCounter
import woowacourse.shopping.domain.model.product.Product
import woowacourse.shopping.domain.model.selector.ItemSelector

object ProductDtoMapper {
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
}
