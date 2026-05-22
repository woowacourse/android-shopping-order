package woowacourse.shopping.data.datasource.remote.cart

import woowacourse.shopping.data.remote.retrofit.dto.CartQuantity
import woowacourse.shopping.data.remote.retrofit.dto.CartRequest
import woowacourse.shopping.data.remote.retrofit.dto.ShoppingCartResponse

interface ShoppingCartRemoteDataSource {
    suspend fun requestCartItems(
        page: Int = DEFAULT_PAGE,
        size: Int = DEFAULT_SIZE,
        sort: List<String>? = null,
    ): ShoppingCartResponse

    suspend fun addCartItem(product: CartRequest)

    suspend fun deleteCartItem(id: Int)

    suspend fun updateQuantityCartItem(
        id: Int,
        product: CartQuantity,
    )

    companion object {
        private const val DEFAULT_PAGE = 0
        private const val DEFAULT_SIZE = 5
    }
}
