package woowacourse.shopping.data.cart.remote.datasource

import woowacourse.shopping.data.cart.remote.CartItemApiService
import woowacourse.shopping.data.cart.remote.dto.CartItemRequest
import woowacourse.shopping.data.cart.remote.dto.CartItemResponse
import woowacourse.shopping.data.common.ResponseHandlingUtils.handleExecute
import woowacourse.shopping.data.common.ResponseResult
import woowacourse.shopping.domain.model.ProductIdsCount

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
