package woowacourse.shopping.data.respository.cart.source.remote

import woowacourse.shopping.data.model.dto.response.CartResponse
import woowacouse.shopping.model.cart.CartProduct

interface CartRemoteDataSource {
    fun requestDatas(
        onFailure: (throwable: Throwable) -> Unit,
        onSuccess: (products: List<CartResponse>) -> Unit,
    )

    fun requestPatchCartItem(
        cartProduct: CartProduct,
        onFailure: (throwable: Throwable) -> Unit,
        onSuccess: () -> Unit,
    )

    fun requestPostCartItem(
        productId: Long,
        onFailure: (throwable: Throwable) -> Unit,
        onSuccess: (Long) -> Unit,
    )

    fun requestDeleteCartItem(cartId: Long)
}
