package woowacourse.shopping.data.cart.model

import com.example.domain.CartProduct

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
