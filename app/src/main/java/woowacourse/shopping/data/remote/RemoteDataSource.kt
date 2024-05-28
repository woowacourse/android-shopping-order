package woowacourse.shopping.data.remote

import retrofit2.Response
import woowacourse.shopping.data.local.entity.CartProductEntity
import woowacourse.shopping.data.remote.dto.request.CartItemRequest
import woowacourse.shopping.data.remote.dto.request.OrderRequest
import woowacourse.shopping.data.remote.dto.request.ProductRequest
import woowacourse.shopping.data.remote.dto.request.QuantityRequest
import woowacourse.shopping.data.remote.dto.response.CartResponse
import woowacourse.shopping.data.remote.dto.response.Product
import woowacourse.shopping.data.remote.dto.response.ProductResponse
import woowacourse.shopping.data.remote.dto.response.QuantityResponse

interface RemoteDataSource {
    fun getProducts(
        page: Int = 0,
        size: Int = 20,
    ): Response<ProductResponse>

    fun postProduct(
        productRequest: ProductRequest
    ): Response<Unit>

    fun getProductById(
        id: Int
    ): Response<Product>

    fun deleteProducyById(
        id: Int
    ): Response<Unit>


    fun getCartItems(
        page: Int = 0,
        size: Int = 20,
    ): Response<CartResponse>

    fun postCartItem(
        cartItemRequest: CartItemRequest
    ): Response<Unit>

    fun deleteCartItem(
        id: Int
    ): Response<Unit>

    fun patchCartItem(
        id: Int,
        quantityRequest: QuantityRequest
    ): Response<Unit>

    fun getCartItemsCounts(

    ): Response<QuantityResponse>

    fun postOrders(
        orderRequest: OrderRequest
    ): Response<Unit>
}
