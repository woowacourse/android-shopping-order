package woowacourse.shopping.data.remote

import retrofit2.Response
import woowacourse.shopping.data.remote.dto.request.CartItemRequest
import woowacourse.shopping.data.remote.dto.request.OrderRequest
import woowacourse.shopping.data.remote.dto.request.ProductRequest
import woowacourse.shopping.data.remote.dto.request.QuantityRequest
import woowacourse.shopping.data.remote.dto.response.Cart
import woowacourse.shopping.data.remote.dto.response.CartResponse
import woowacourse.shopping.data.remote.dto.response.Product
import woowacourse.shopping.data.remote.dto.response.QuantityResponse

interface RemoteDataSource {
    suspend fun getProducts(
        category: String? = null,
        page: Int = 0,
        size: Int = 20,
    ): Result<List<Product>>
    // 현재 DTO를 반환해주고 있는데 DataSource에서 DTO를 반환하는게 모델로 변경해서 반환

    suspend fun addProduct(productRequest: ProductRequest)

    suspend fun getProductById(id: Int): Result<Product>

    suspend fun deleteProductById(id: Int)

    suspend fun getCartItems(
        page: Int = 0,
        size: Int = 20,
    ): Result<List<Cart>>

    // todo header의 id값은 어떻게 가져올 것인가?
    suspend fun postCartItem(cartItemRequest: CartItemRequest): Result<Response<Unit>>

    suspend fun deleteCartItem(id: Int): Result<Unit>

    suspend fun patchCartItem(
        id: Int,
        quantityRequest: QuantityRequest,
    ): Result<Unit>

    suspend fun getCartItemsCounts() : Result<QuantityResponse>

    suspend fun postOrders(orderRequest: OrderRequest): Result<Unit>
}
