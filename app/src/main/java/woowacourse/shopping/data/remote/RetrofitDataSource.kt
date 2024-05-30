package woowacourse.shopping.data.remote

import retrofit2.Response
import woowacourse.shopping.data.remote.dto.request.CartItemRequest
import woowacourse.shopping.data.remote.dto.request.OrderRequest
import woowacourse.shopping.data.remote.dto.request.ProductRequest
import woowacourse.shopping.data.remote.dto.request.QuantityRequest
import woowacourse.shopping.data.remote.dto.response.CartResponse
import woowacourse.shopping.data.remote.dto.response.Product
import woowacourse.shopping.data.remote.dto.response.ProductResponse
import woowacourse.shopping.data.remote.dto.response.QuantityResponse
import woowacourse.shopping.data.remote.service.CartItemApi
import woowacourse.shopping.data.remote.service.OrderApi
import woowacourse.shopping.data.remote.service.ProductApi

class RetrofitDataSource(
    private val productApi: ProductApi = RetrofitModule.productApi,
    private val cartItemApi: CartItemApi = RetrofitModule.cartItemsApi,
    private val orderApi: OrderApi = RetrofitModule.orderApi
    ): RemoteDataSource {
    override fun getProducts(category: String?, page: Int, size: Int): Response<ProductResponse> {
        return productApi.getProducts(category= category, page = page, size = size).execute()
    }

    override fun postProduct(productRequest: ProductRequest): Response<Unit> {
        return productApi.postProduct(productRequest = productRequest).execute()
    }

    override fun getProductById(id: Int): Response<Product> {
        return productApi.getProductById(id = id).execute()
    }

    override fun deleteProducyById(id: Int): Response<Unit> {
        return productApi.deleteProductById(id = id).execute()
    }

    override fun getCartItems(page: Int, size: Int): Response<CartResponse> {
        return cartItemApi.getCartItems(page = page, size = size).execute()
    }

    override fun postCartItem(cartItemRequest: CartItemRequest): Response<Unit> {
        return cartItemApi.postCartItem(cartItemRequest = cartItemRequest).execute()
    }

    override fun deleteCartItem(id: Int): Response<Unit> {
        return cartItemApi.deleteCartItem(id = id).execute()
    }

    override fun patchCartItem(id: Int, quantityRequest: QuantityRequest): Response<Unit> {
        return cartItemApi.patchCartItem(id = id, quantityRequest = quantityRequest).execute()
    }

    override fun getCartItemsCounts(): Response<QuantityResponse> {
        return cartItemApi.getCartItemsCounts().execute()
    }

    override fun postOrders(orderRequest: OrderRequest): Response<Unit> {
        return orderApi.postOrders(orderRequest = orderRequest).execute()
    }
}