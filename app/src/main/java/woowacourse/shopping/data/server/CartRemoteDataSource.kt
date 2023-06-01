package woowacourse.shopping.data.server

import woowacourse.shopping.domain.CartProduct

interface CartRemoteDataSource {
    fun getCartProducts(onSuccess: (List<CartProduct>) -> Unit, onFailure: () -> Unit)

    fun addCartProduct(id: Int, onSuccess: (Int) -> Unit, onFailure: () -> Unit)

    fun updateCartProductQuantity(id: Int, quantity: Int, onSuccess: () -> Unit, onFailure: () -> Unit)

    fun deleteCartProduct(cartProductId: Int, onSuccess: () -> Unit, onFailure: () -> Unit)
}