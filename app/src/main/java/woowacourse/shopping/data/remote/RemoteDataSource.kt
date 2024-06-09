package woowacourse.shopping.data.remote

import retrofit2.Response
import woowacourse.shopping.data.remote.dto.request.CartItemRequest
import woowacourse.shopping.data.remote.dto.request.OrderRequest
import woowacourse.shopping.data.remote.dto.request.ProductRequest
import woowacourse.shopping.data.remote.dto.request.QuantityRequest
import woowacourse.shopping.data.remote.dto.response.CartsResponse
import woowacourse.shopping.data.remote.dto.response.CouponResponse
import woowacourse.shopping.data.remote.dto.response.ProductResponse
import woowacourse.shopping.data.remote.dto.response.ProductsResponse
import woowacourse.shopping.data.remote.dto.response.QuantityResponse

interface RemoteDataSource {
    suspend fun getProducts(
        category: String? = null,
        page: Int = 0,
        size: Int = 20,
    ): Response<ProductsResponse>

    suspend fun postProduct(productRequest: ProductRequest): Response<Unit>

    suspend fun getProductById(id: Int): Response<ProductResponse>

    suspend fun deleteProductById(id: Int): Response<Unit>

    suspend fun getCartItems(
        page: Int = 0,
        size: Int = 20,
    ): Response<CartsResponse>

    suspend fun postCartItem(cartItemRequest: CartItemRequest): Response<Unit>

    suspend fun deleteCartItem(id: Int): Response<Unit>

    suspend fun patchCartItem(
        id: Int,
        quantityRequestDto: QuantityRequest,
    ): Response<Unit>

    suspend fun getCartItemsCounts(): Response<QuantityResponse>

    suspend fun postOrders(orderRequest: OrderRequest): Response<Unit>

    suspend fun getCoupons(): Response<List<CouponResponse>>
}
