package woowacourse.shopping.data.datasource.cart

import woowacourse.shopping.domain.model.CartProduct

interface CartProductDataSource {
    fun requestCartProducts(
        token: String,
        onSuccess: (List<CartProduct>) -> Unit,
        onFailure: (String) -> Unit,
    )

    fun addCartProductByProductId(
        token: String,
        productId: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    )

    fun updateCartProductCountById(
        token: String,
        cartItemId: String,
        quantity: Int,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    )

    fun deleteCartProductById(
        token: String,
        cartItemId: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    )

    fun requestCartProductByProductId(
        token: String,
        productId: Int,
        onSuccess: (CartProduct) -> Unit,
        onFailure: (String) -> Unit,
    )
}
