package woowacourse.shopping.data.remote

import retrofit2.Response
import woowacourse.shopping.data.remote.dto.request.CartItemRequest
import woowacourse.shopping.data.remote.dto.request.OrderRequest
import woowacourse.shopping.data.remote.dto.request.ProductRequest
import woowacourse.shopping.data.remote.dto.request.QuantityRequest
import woowacourse.shopping.data.remote.dto.response.CartResponse
import woowacourse.shopping.data.remote.dto.response.Product
import woowacourse.shopping.data.remote.dto.response.QuantityResponse

interface RemoteDataSource {
    suspend fun getProducts(
        category: String? = null,
        page: Int = 0,
        size: Int = 20,
    ) : Result<List<Product>>
    // 현재 DTO를 반환해주고 있는데 DataSource에서 DTO를 반환하는게 모델로 변경해서 반환

    suspend fun addProduct(productRequest: ProductRequest)

    suspend fun getProductById(id: Int): Result<Product>

    suspend fun deleteProductById(id: Int)

    fun getCartItems(
        page: Int = 0,
        size: Int = 20,
        callback: (Result<CartResponse>) -> Unit,
    )

    fun postCartItem(cartItemRequest: CartItemRequest): Response<Unit>

    fun deleteCartItem(id: Int): Response<Unit>

    fun patchCartItem(
        id: Int,
        quantityRequest: QuantityRequest,
    ): Response<Unit>

    fun getCartItemsCounts(callback: (Result<QuantityResponse>) -> Unit)

    fun postOrders(orderRequest: OrderRequest): Response<Unit>
}
