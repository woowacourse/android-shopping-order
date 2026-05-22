package woowacourse.shopping.data.remote.retrofit.repository

import woowacourse.shopping.data.remote.retrofit.api.ShoppingCartRetrofitInterface
import woowacourse.shopping.data.remote.retrofit.dto.CartQuantity
import woowacourse.shopping.data.remote.retrofit.dto.CartRequest
import woowacourse.shopping.data.remote.retrofit.dto.ShoppingCartResponse

class ShoppingCartRetrofitRepository(
    private val apiService: ShoppingCartRetrofitInterface,
) {
    suspend fun requestCartItems(
        page: Int = DEFAULT_PAGE,
        size: Int = DEFAULT_SIZE,
        sort: List<String>? = null,
    ): ShoppingCartResponse {
        return apiService.requestCartItems(
            page = page,
            size = size,
            sort = sort,
        )
    }

    suspend fun addCartItem(product: CartRequest): Unit =
        apiService.addCartItem(
            product = product,
        )

    suspend fun deleteCartItem(id: Int): Unit =
        apiService.deleteCartItem(
            id = id,
        )

    suspend fun updateQuantityCartItem(
        id: Int,
        product: CartQuantity,
    ): Unit =
        apiService.updateQuantityCartItem(
            id = id,
            product = product,
        )

    companion object {
        private const val DEFAULT_PAGE = 0
        private const val DEFAULT_SIZE = 5
    }
}
