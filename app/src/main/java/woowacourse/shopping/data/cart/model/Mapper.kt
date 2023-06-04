package woowacourse.shopping.data.cart.model

import com.example.domain.CartProduct
import woowacourse.shopping.data.cart.model.dto.response.CartProductResponse

fun CartProductResponse.toDomain(): CartProduct {
    return CartProduct(
        id = id,
        productId = product.id,
        productImageUrl = product.imageUrl,
        productName = product.name,
        productPrice = product.price,
        quantity = quantity,
        isPicked = true
    )
}
