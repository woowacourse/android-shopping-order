package woowacourse.shopping.data.respository.cart.source.remote

import woowacouse.shopping.model.cart.CartProduct

interface CartRemoteDataSource {
    fun requestDatas(
        onFailure: () -> Unit,
        onSuccess: (products: List<CartProduct>) -> Unit,
    )

    fun requestPatchCartItem(
        cartProduct: CartProduct,
        onFailure: () -> Unit,
        onSuccess: () -> Unit,
    )

    fun requestPostCartItem(
        productId: Long,
        onFailure: () -> Unit,
        onSuccess: (Long) -> Unit,
    )

    fun requestDeleteCartItem(cartId: Long)
}
