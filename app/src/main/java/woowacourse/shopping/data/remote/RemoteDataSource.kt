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
    fun getProducts(
        category: String? = null,
        page: Int = 0,
        size: Int = 20,
    ): Response<ProductResponseDto>

    fun postProduct(productRequestDto: ProductRequestDto): Response<Unit>

    fun getProductById(id: Int): Response<Product>

    fun deleteProductById(id: Int): Response<Unit>

    fun getCartItems(
        page: Int = 0,
        size: Int = 20,
    ): Response<CartResponseDto>

    fun postCartItem(cartItemRequestDto: CartItemRequestDto): Response<Unit>

    fun deleteCartItem(id: Int): Response<Unit>

    fun patchCartItem(
        id: Int,
        quantityRequestDto: QuantityRequestDto,
    ): Response<Unit>

    fun getCartItemsCounts(): Response<QuantityResponseDto>

    fun postOrders(orderRequestDto: OrderRequestDto): Response<Unit>
}
