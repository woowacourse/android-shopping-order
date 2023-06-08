package woowacourse.shopping.data.respository.cart.source.remote

import woowacourse.shopping.data.model.CartRemoteEntity

interface CartRemoteDataSource {
    fun requestDatas(
        onFailure: () -> Unit,
        onSuccess: (products: List<CartRemoteEntity>) -> Unit,
    )

    fun requestPatchCartItem(
        cartEntity: CartRemoteEntity,
        onFailure: () -> Unit,
        onSuccess: () -> Unit,
    )

    fun requestPostCartItem(
        productId: Long,
        onFailure: () -> Unit,
        onSuccess: (cartId: Long) -> Unit,
    )

    fun requestDeleteCartItem(cartId: Long)
}
