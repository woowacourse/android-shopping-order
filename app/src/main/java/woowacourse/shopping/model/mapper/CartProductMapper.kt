package woowacourse.shopping.model.mapper

import com.example.domain.CartProduct
import woowacourse.shopping.model.CartProductState

fun CartProduct.toUi(): CartProductState {
    return CartProductState(id, productId, productImageUrl, productName, productPrice, quantity, isPicked)
}

fun CartProductState.toDomain(): CartProduct {
    return CartProduct(id, productId, productImageUrl, productName, productPrice, quantity, isPicked)
}
