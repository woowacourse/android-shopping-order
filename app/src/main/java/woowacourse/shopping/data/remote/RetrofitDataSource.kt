package woowacourse.shopping.data.remote

import retrofit2.Response
import woowacourse.shopping.data.remote.dto.request.CartItemRequest
import woowacourse.shopping.data.remote.dto.request.OrderRequest
import woowacourse.shopping.data.remote.dto.request.ProductRequest
import woowacourse.shopping.data.remote.dto.request.QuantityRequest
import woowacourse.shopping.data.remote.dto.response.Cart
import woowacourse.shopping.data.remote.dto.response.Coupons
import woowacourse.shopping.data.remote.dto.response.Product
import woowacourse.shopping.data.remote.dto.response.QuantityResponse
import woowacourse.shopping.data.remote.service.CartItemApi
import woowacourse.shopping.data.remote.service.CouponApi
import woowacourse.shopping.data.remote.service.OrderApi
import woowacourse.shopping.data.remote.service.ProductApi

class RetrofitDataSource(
    private val productApi: ProductApi = RetrofitModule.productApi,
    private val cartItemApi: CartItemApi = RetrofitModule.cartItemsApi,
    private val orderApi: OrderApi = RetrofitModule.orderApi,
    private val couponApi: CouponApi = RetrofitModule.couponApi,
) : RemoteDataSource {
    override suspend fun getProducts(
        category: String?,
        page: Int,
        size: Int,
    ): Result<List<Product>> =
        runCatching {
            productApi.getProducts(category = category, page = page, size = size).content
        }

    override suspend fun addProduct(productRequest: ProductRequest) {
        productApi.addProduct(productRequest = productRequest)
    }

    override suspend fun getProductById(id: Int): Result<Product> =
        runCatching {
            productApi.getProductById(id = id)
        }

    override suspend fun deleteProductById(id: Int) {
        productApi.deleteProductById(id = id)
    }

    override suspend fun getCartItems(
        page: Int,
        size: Int,
    ): Result<List<Cart>> =
        runCatching {
            cartItemApi.getCartItems(page = page, size = size).content
        }

    override suspend fun addCartItem(cartItemRequest: CartItemRequest): Result<Response<Unit>> =
        runCatching {
            cartItemApi.addCartItem(cartItemRequest = cartItemRequest)
        }

    override suspend fun deleteCartItem(id: Int): Result<Unit> =
        runCatching {
            cartItemApi.deleteCartItem(id = id)
        }

    override suspend fun updateCartItem(
        id: Int,
        quantityRequest: QuantityRequest,
    ): Result<Unit> =
        runCatching {
            cartItemApi.updateCartItem(id = id, quantityRequest = quantityRequest)
        }

    override suspend fun getCartItemsCounts(): Result<QuantityResponse> =
        runCatching {
            cartItemApi.getCartItemsCounts()
        }

    override suspend fun submitOrders(orderRequest: OrderRequest): Result<Unit> =
        runCatching {
            orderApi.submitOrders(orderRequest = orderRequest)
        }

    override suspend fun getCoupons(): Result<List<Coupons>> =
        runCatching {
            couponApi.getCoupons()
        }
}
