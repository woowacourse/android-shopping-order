package woowacourse.shopping.data.datasource.cart

import woowacourse.shopping.domain.model.CartProduct

interface CartProductDataSource {
    fun requestCartProducts(
        token: String,
        onSuccess: (List<CartProduct>) -> Unit,
        onFailure: () -> Unit,
    ) // List<CartProductDto>

    fun addCartProductByProductId(
        token: String,
        productId: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) // : Call<Int>

    fun updateCartProductCountById(
        token: String,
        cartItemId: String,
        quantity: Int,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) // : Call<Void>

    fun deleteCartProductById(
        token: String,
        cartItemId: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) // : Call<CartProductDto>

    // fun requestCartProductById(productId: Int): CartProduct?
}
