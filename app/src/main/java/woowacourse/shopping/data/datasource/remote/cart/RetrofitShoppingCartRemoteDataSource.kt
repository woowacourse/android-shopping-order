package woowacourse.shopping.data.datasource.remote.cart

import woowacourse.shopping.data.remote.retrofit.api.ShoppingCartRetrofitInterface
import woowacourse.shopping.data.remote.retrofit.dto.CartQuantity
import woowacourse.shopping.data.remote.retrofit.dto.CartRequest
import woowacourse.shopping.data.remote.retrofit.dto.ShoppingCartResponse

class RetrofitShoppingCartRemoteDataSource(
    private val apiService: ShoppingCartRetrofitInterface,
) : ShoppingCartRemoteDataSource {
    override suspend fun requestCartItems(
        page: Int,
        size: Int,
        sort: List<String>?,
    ): ShoppingCartResponse =
        apiService.requestCartItems(
            page = page,
            size = size,
            sort = sort,
        )

    override suspend fun addCartItem(product: CartRequest): Unit =
        apiService.addCartItem(
            product = product,
        )

    override suspend fun deleteCartItem(id: Int): Unit =
        apiService.deleteCartItem(
            id = id,
        )

    override suspend fun updateQuantityCartItem(
        id: Int,
        product: CartQuantity,
    ): Unit =
        apiService.updateQuantityCartItem(
            id = id,
            product = product,
        )
}
