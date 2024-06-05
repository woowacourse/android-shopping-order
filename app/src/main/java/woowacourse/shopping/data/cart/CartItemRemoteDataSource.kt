package woowacourse.shopping.data.cart

import woowacourse.shopping.data.ResponseResult
import woowacourse.shopping.data.handleResponseResult
import woowacourse.shopping.domain.model.ProductIdsCount
import woowacourse.shopping.remote.cart.CartItemApiService
import woowacourse.shopping.remote.cart.CartItemRequest
import woowacourse.shopping.remote.cart.CartItemResponse

class CartItemRemoteDataSource(private val cartItemApiService: CartItemApiService) :
    CartItemDataSource {

    override fun loadAllCartItems(): ResponseResult<CartItemResponse> =
        handleResponseResult { cartItemApiService.requestCartItems().execute() }

    override fun addedNewProductsId(productIdsCount: ProductIdsCount): ResponseResult<Unit> =
        handleResponseResult { cartItemApiService.addCartItem(CartItemRequest(productIdsCount.productId, productIdsCount.quantity)).execute() }

    // TODO: 동작 안됨
    override fun removedProductsId(cartItemId: Long): Long {
        cartItemApiService.removeCartItem(cartItemId)
        return 10
    }

    override fun plusProductsIdCount(
        cartItemId: Long,
        quantity: Int,
    ): ResponseResult<Unit> = handleResponseResult { cartItemApiService.updateCartItemQuantity(cartItemId, quantity).execute() }

    override fun minusProductsIdCount(
        cartItemId: Long,
        quantity: Int,
    ): ResponseResult<Unit> = handleResponseResult { cartItemApiService.updateCartItemQuantity(cartItemId, quantity).execute() }

    override fun clearAll() {
        TODO("Not yet implemented")
    }

    companion object {
        private const val TAG = "CartItemRemoteDataSource"
    }
}
