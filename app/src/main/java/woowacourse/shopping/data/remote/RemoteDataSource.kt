package woowacourse.shopping.data.remote

import retrofit2.Response
import woowacourse.shopping.data.remote.dto.request.CartItemRequestDto
import woowacourse.shopping.data.remote.dto.request.OrderRequestDto
import woowacourse.shopping.data.remote.dto.request.ProductRequestDto
import woowacourse.shopping.data.remote.dto.request.QuantityRequestDto
import woowacourse.shopping.data.remote.dto.response.CartResponseDto
import woowacourse.shopping.data.remote.dto.response.Product
import woowacourse.shopping.data.remote.dto.response.ProductResponseDto
import woowacourse.shopping.data.remote.dto.response.QuantityResponseDto

interface RemoteDataSource {
    suspend fun getProducts(
        category: String? = null,
        page: Int = 0,
        size: Int = 20,
    ): Response<ProductResponseDto>

    suspend fun postProduct(productRequestDto: ProductRequestDto): Response<Unit>

    suspend fun getProductById(id: Int): Response<Product>

    suspend fun deleteProductById(id: Int): Response<Unit>

    suspend fun getCartItems(
        page: Int = 0,
        size: Int = 20,
    ): Response<CartResponseDto>

    suspend fun postCartItem(cartItemRequestDto: CartItemRequestDto): Response<Unit>

    suspend fun deleteCartItem(id: Int): Response<Unit>

    suspend fun patchCartItem(
        id: Int,
        quantityRequestDto: QuantityRequestDto,
    ): Response<Unit>

    suspend fun getCartItemsCounts(): Response<QuantityResponseDto>

    suspend fun postOrders(orderRequestDto: OrderRequestDto): Response<Unit>
}
