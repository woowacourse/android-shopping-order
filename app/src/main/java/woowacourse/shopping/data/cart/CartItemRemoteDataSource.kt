package woowacourse.shopping.data.cart

import woowacourse.shopping.data.common.ResponseHandlingUtils.handleExecute
import woowacourse.shopping.data.common.ResponseResult
import woowacourse.shopping.domain.model.ProductIdsCount
import woowacourse.shopping.remote.cart.CartItemApiService
import woowacourse.shopping.remote.cart.CartItemRequest
import woowacourse.shopping.remote.cart.CartItemResponse

class CartItemRemoteDataSource(private val cartItemApiService: CartItemApiService) :
    CartItemDataSource {
    override suspend fun fetchCartItems(): ResponseResult<CartItemResponse> =
        handleExecute { cartItemApiService.requestCartItems() }

    override suspend fun saveCartItem(productIdsCount: ProductIdsCount): ResponseResult<Unit> =
        handleExecute {
            cartItemApiService.addCartItem(
                CartItemRequest(productIdsCount.productId, productIdsCount.quantity),
            )
        }

    override suspend fun deleteCartItem(cartItemId: Long): ResponseResult<Unit> =
        handleExecute { cartItemApiService.removeCartItem(cartItemId)}

    override suspend fun updateCartItemQuantity(
        cartItemId: Long,
        quantity: Int,
    ): ResponseResult<Unit> = handleExecute { cartItemApiService.updateCartItemQuantity(cartItemId, quantity) }

    companion object {
        private const val TAG = "CartItemRemoteDataSource"
    }
}
