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
import woowacourse.shopping.data.remote.service.CartItemApi
import woowacourse.shopping.data.remote.service.CouponApi
import woowacourse.shopping.data.remote.service.OrderApi
import woowacourse.shopping.data.remote.service.ProductApi

class RetrofitDataSource(
    private val productApi: ProductApi = ProductApi.service(),
    private val cartItemApi: CartItemApi = CartItemApi.service(),
    private val orderApi: OrderApi = OrderApi.service(),
    private val couponApi: CouponApi = CouponApi.service()
) : RemoteDataSource {
    override suspend fun getProducts(
        category: String?,
        page: Int,
        size: Int,
    ): Response<ProductsResponse> {
        return productApi.getProducts(category = category, page = page, size = size)
    }

    override suspend fun postProduct(productRequest: ProductRequest): Response<Unit> {
        return productApi.postProduct(productRequest = productRequest)
    }

    override suspend fun getProductById(id: Int): Response<ProductResponse> {
        return productApi.getProductById(id = id)
    }

    override suspend fun deleteProductById(id: Int): Response<Unit> {
        return productApi.deleteProductById(id = id)
    }

    override suspend fun getCartItems(
        page: Int,
        size: Int,
    ): Response<CartsResponse> {
        return cartItemApi.getCartItems(page = page, size = size)
    }

    override suspend fun postCartItem(cartItemRequest: CartItemRequest): Response<Unit> {
        return cartItemApi.postCartItem(cartItemRequest = cartItemRequest)
    }

    override suspend fun deleteCartItem(id: Int): Response<Unit> {
        return cartItemApi.deleteCartItem(id = id)
    }

    override suspend fun patchCartItem(
        id: Int,
        quantityRequestDto: QuantityRequest,
    ): Response<Unit> {
        return cartItemApi.patchCartItem(id = id, quantityRequestDto = quantityRequestDto)
    }

    override suspend fun getCartItemsCounts(): Response<QuantityResponse> {
        return cartItemApi.getCartItemsCounts()
    }

    override suspend fun postOrders(orderRequest: OrderRequest): Response<Unit> {
        return orderApi.postOrders(orderRequest = orderRequest)
    }

    override suspend fun getCoupons(): Response<List<CouponResponse>> {
        return couponApi.getCoupons()
    }
}
