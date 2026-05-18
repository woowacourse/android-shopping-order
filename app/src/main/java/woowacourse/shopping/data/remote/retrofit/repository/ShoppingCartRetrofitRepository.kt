package woowacourse.shopping.data.remote.retrofit.repository

import retrofit2.Call
import woowacourse.shopping.data.remote.retrofit.api.ShoppingCartRetrofitInterface
import woowacourse.shopping.data.remote.retrofit.dto.CartQuantity
import woowacourse.shopping.data.remote.retrofit.dto.CartRequest
import woowacourse.shopping.data.remote.retrofit.dto.ShoppingCartResponse

class ShoppingCartRetrofitRepository(
    private val apiService: ShoppingCartRetrofitInterface,
) {
    fun requestCartItems(
        page: Int = DEFAULT_PAGE,
        size: Int = DEFAULT_SIZE,
        sort: List<String>? = null,
    ): Call<ShoppingCartResponse> =
        apiService.requestCartItems(
            page = page,
            size = size,
            sort = sort,
        )

    fun addCartItem(product: CartRequest): Call<Void> =
        apiService.addCartItem(
            product = product,
        )

    fun deleteCartItem(id: Int): Call<Void> =
        apiService.deleteCartItem(
            id = id,
        )

    fun updateQuantityCartItem(
        id: Int,
        product: CartQuantity,
    ): Call<Void> =
        apiService.updateQuantityCartItem(
            id = id,
            product = product,
        )

    companion object {
        private const val DEFAULT_PAGE = 0
        private const val DEFAULT_SIZE = 5
    }
}
