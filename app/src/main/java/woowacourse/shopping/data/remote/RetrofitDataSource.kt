package woowacourse.shopping.data.remote

import retrofit2.Response
import woowacourse.shopping.data.remote.dto.request.CartItemRequestDto
import woowacourse.shopping.data.remote.dto.request.OrderRequestDto
import woowacourse.shopping.data.remote.dto.request.ProductRequestDto
import woowacourse.shopping.data.remote.dto.request.QuantityRequestDto
import woowacourse.shopping.data.remote.dto.response.CartResponseDto
import woowacourse.shopping.data.remote.dto.response.CouponResponseDto
import woowacourse.shopping.data.remote.dto.response.Product
import woowacourse.shopping.data.remote.dto.response.ProductResponseDto
import woowacourse.shopping.data.remote.dto.response.QuantityResponseDto
import woowacourse.shopping.data.remote.service.CartItemApi
import woowacourse.shopping.data.remote.service.CouponApi
import woowacourse.shopping.data.remote.service.OrderApi
import woowacourse.shopping.data.remote.service.ProductApi

class RetrofitDataSource(
    private val productApi: ProductApi = RetrofitModule.productApi,
    private val cartItemApi: CartItemApi = RetrofitModule.cartItemsApi,
    private val orderApi: OrderApi = RetrofitModule.orderApi,
    private val couponApi: CouponApi = RetrofitModule.couponApi
) : RemoteDataSource {
    override suspend fun getProducts(
        category: String?,
        page: Int,
        size: Int,
    ): Response<ProductResponseDto> {
        return productApi.getProducts(category = category, page = page, size = size)
    }

    override suspend fun postProduct(productRequestDto: ProductRequestDto): Response<Unit> {
        return productApi.postProduct(productRequestDto = productRequestDto)
    }

    override suspend fun getProductById(id: Int): Response<Product> {
        return productApi.getProductById(id = id)
    }

    override suspend fun deleteProductById(id: Int): Response<Unit> {
        return productApi.deleteProductById(id = id)
    }

    override suspend fun getCartItems(
        page: Int,
        size: Int,
    ): Response<CartResponseDto> {
        return cartItemApi.getCartItems(page = page, size = size)
    }

    override suspend fun postCartItem(cartItemRequestDto: CartItemRequestDto): Response<Unit> {
        return cartItemApi.postCartItem(cartItemRequestDto = cartItemRequestDto)
    }

    override suspend fun deleteCartItem(id: Int): Response<Unit> {
        return cartItemApi.deleteCartItem(id = id)
    }

    override suspend fun patchCartItem(
        id: Int,
        quantityRequestDto: QuantityRequestDto,
    ): Response<Unit> {
        return cartItemApi.patchCartItem(id = id, quantityRequestDto = quantityRequestDto)
    }

    override suspend fun getCartItemsCounts(): Response<QuantityResponseDto> {
        return cartItemApi.getCartItemsCounts()
    }

    override suspend fun postOrders(orderRequestDto: OrderRequestDto): Response<Unit> {
        return orderApi.postOrders(orderRequestDto = orderRequestDto)
    }

    override suspend fun getCoupons(): Response<List<CouponResponseDto>> {
        return couponApi.getCoupons()
    }
}
