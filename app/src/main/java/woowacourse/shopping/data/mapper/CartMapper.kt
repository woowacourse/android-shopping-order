package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.response.CartItemsResponse
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductDetail

fun CartItemsResponse.Content.toDomain(): Product =
    Product(
        productDetail =
            ProductDetail(
                id = product.id,
                name = product.name,
                imageUrl = product.imageUrl,
                price = product.price,
                category = product.category,
            ),
        cartId = id,
        quantity = quantity,
    )
