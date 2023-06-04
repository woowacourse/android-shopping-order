package woowacourse.shopping.model.mapper

import com.example.domain.CartProduct
import woowacourse.shopping.model.CartProductState

fun CartProduct.toUi(): CartProductState {
    return CartProductState(
        id = id,
        productId = productId,
        productImageUrl = productImageUrl,
        productName = productName,
        productPrice = productPrice,
        quantity = quantity,
        isPicked = isPicked
    )
}

fun CartProductState.toDomain(): CartProduct {
    return CartProduct(
        id = id,
        productId = productId,
        productImageUrl = productImageUrl,
        productName = productName,
        productPrice = productPrice,
        quantity = quantity,
        isPicked = isPicked
    )
}
