package woowacourse.shopping.data.respository.cart.source.remote

import woowacourse.shopping.data.model.dto.response.CartResponse
import woowacourse.shopping.data.respository.cart.source.service.CartService
import woowacourse.shopping.data.util.responseBodyCallback
import woowacourse.shopping.data.util.responseHeaderLocationCallback
import woowacouse.shopping.model.cart.CartProduct

class CartRemoteDataSourceImpl(
    private val cartService: CartService,
) : CartRemoteDataSource {
    override fun requestDatas(
        onFailure: (throwable: Throwable) -> Unit,
        onSuccess: (products: List<CartResponse>) -> Unit,
    ) {
        cartService.requestDatas().enqueue(
            responseBodyCallback(
                onFailure = onFailure,
                onSuccess = onSuccess,
            )
        )
    }

    override fun requestPatchCartItem(
        cartProduct: CartProduct,
        onFailure: (throwable: Throwable) -> Unit,
        onSuccess: () -> Unit,
    ) {
        cartService.requestPatchCartItem(cartProduct.id, cartProduct.count).enqueue(
            responseBodyCallback(
                onFailure = onFailure,
            ) {
                onSuccess()
            }
        )
    }

    override fun requestPostCartItem(
        productId: Long,
        onFailure: (throwable: Throwable) -> Unit,
        onSuccess: (Long) -> Unit,
    ) {
        cartService.requestPostCartItem(productId).enqueue(
            responseHeaderLocationCallback(
                onFailure = onFailure,
            ) {
                onSuccess(it)
            }
        )
    }

    override fun requestDeleteCartItem(cartId: Long) {
        val thread = Thread {
            cartService.requestDeleteCartItem(cartId).execute()
        }
        thread.start()
        thread.join()
    }
}
