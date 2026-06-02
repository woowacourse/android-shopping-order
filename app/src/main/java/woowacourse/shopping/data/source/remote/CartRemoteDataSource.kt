package woowacourse.shopping.data.source.remote

import woowacourse.shopping.data.source.remote.api.CartService
import woowacourse.shopping.data.source.remote.api.safeNetworkApiCall
import woowacourse.shopping.data.source.remote.dto.cart.request.AddItemRequest
import woowacourse.shopping.data.source.remote.dto.cart.request.QuantityRequest
import woowacourse.shopping.data.source.remote.dto.cart.response.CartContent
import woowacourse.shopping.error.NetworkError
import woowacourse.shopping.error.Result

class CartRemoteDataSource(
    private val cartService: CartService,
) {
    suspend fun getCartItems(
        page: Int,
        size: Int,
    ): Result<List<CartContent>, NetworkError> =
        safeNetworkApiCall {
            cartService
                .requestItems(
                    page = page,
                    size = size,
                ).cartContent
        }

    suspend fun addItem(
        id: Long,
        quantity: Int,
    ): Result<Unit, NetworkError> =
        safeNetworkApiCall {
            cartService.requestAddItem(
                addItemRequest = AddItemRequest(id, quantity),
            )
        }

    suspend fun deleteItem(id: Long): Result<Unit, NetworkError> =
        safeNetworkApiCall {
            cartService.requestDeleteItem(
                id = id,
            )
        }

    suspend fun changeQuantity(
        id: Long,
        quantity: Int,
    ): Result<Unit, NetworkError> =
        safeNetworkApiCall {
            cartService.requestChangeQuantity(
                id = id,
                quantity = QuantityRequest(quantity),
            )
        }
}
