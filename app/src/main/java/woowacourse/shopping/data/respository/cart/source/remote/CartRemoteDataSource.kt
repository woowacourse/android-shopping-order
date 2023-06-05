package woowacourse.shopping.data.respository.cart.source.remote

import woowacouse.shopping.model.cart.CartProduct

interface CartRemoteDataSource {
    fun requestDatas(
        onFailure: (message: String) -> Unit,
        onSuccess: (products: List<CartProduct>) -> Unit,
    )

    fun requestPatchCartItem(
        cartProduct: CartProduct,
        onFailure: (message: String) -> Unit,
        onSuccess: () -> Unit,
    )

    fun requestPostCartItem(
        productId: Long,
        onFailure: (message: String) -> Unit,
        onSuccess: (Long) -> Unit,
    )

    fun requestDeleteCartItem(cartId: Long)
}
