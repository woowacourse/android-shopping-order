package woowacourse.shopping.data.server

import woowacourse.shopping.domain.CartProduct

interface CartRemoteDataSource {
    fun getCartProducts(onSuccess: (List<CartProduct>) -> Unit, onFailure: (String) -> Unit)

    fun addCartProduct(id: Int, quantity: Int, onSuccess: (Int) -> Unit, onFailure: (String) -> Unit)

    fun updateCartProductQuantity(id: Int, quantity: Int, onSuccess: () -> Unit, onFailure: (String) -> Unit)

    fun deleteCartProduct(cartProductId: Int, onSuccess: () -> Unit, onFailure: (String) -> Unit)
}