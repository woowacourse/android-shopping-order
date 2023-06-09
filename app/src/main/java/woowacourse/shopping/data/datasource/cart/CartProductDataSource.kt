package woowacourse.shopping.data.datasource.cart

import woowacourse.shopping.domain.model.CartProduct

interface CartProductDataSource {
    fun requestCartProducts(
        onSuccess: (List<CartProduct>) -> Unit,
        onFailure: (String) -> Unit,
    )

    fun addCartProductByProductId(
        productId: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    )

    fun updateCartProductCountById(
        cartItemId: String,
        quantity: Int,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    )

    fun deleteCartProductById(
        cartItemId: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    )

    fun requestCartProductByProductId(
        productId: Int,
        onSuccess: (CartProduct) -> Unit,
        onFailure: (String) -> Unit,
    )
}
