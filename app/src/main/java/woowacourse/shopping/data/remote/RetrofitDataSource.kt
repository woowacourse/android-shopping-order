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
import woowacourse.shopping.data.remote.service.CartItemApi
import woowacourse.shopping.data.remote.service.OrderApi
import woowacourse.shopping.data.remote.service.ProductApi

class RetrofitDataSource(
    private val productApi: ProductApi = RetrofitModule.productApi,
    private val cartItemApi: CartItemApi = RetrofitModule.cartItemsApi,
    private val orderApi: OrderApi = RetrofitModule.orderApi,
) : RemoteDataSource {
    override fun getProducts(
        category: String?,
        page: Int,
        size: Int,
    ): Response<ProductResponseDto> {
        return productApi.getProducts(category = category, page = page, size = size).execute()
    }

    override fun postProduct(productRequestDto: ProductRequestDto): Response<Unit> {
        return productApi.postProduct(productRequestDto = productRequestDto).execute()
    }

    override fun getProductById(id: Int): Response<Product> {
        return productApi.getProductById(id = id).execute()
    }

    override fun deleteProductById(id: Int): Response<Unit> {
        return productApi.deleteProductById(id = id).execute()
    }

    override fun getCartItems(
        page: Int,
        size: Int,
    ): Response<CartResponseDto> {
        return cartItemApi.getCartItems(page = page, size = size).execute()
    }

    override fun postCartItem(cartItemRequestDto: CartItemRequestDto): Response<Unit> {
        return cartItemApi.postCartItem(cartItemRequestDto = cartItemRequestDto).execute()
    }

    override fun deleteCartItem(id: Int): Response<Unit> {
        return cartItemApi.deleteCartItem(id = id).execute()
    }

    override fun patchCartItem(
        id: Int,
        quantityRequestDto: QuantityRequestDto,
    ): Response<Unit> {
        return cartItemApi.patchCartItem(id = id, quantityRequestDto = quantityRequestDto).execute()
    }

    override fun getCartItemsCounts(): Response<QuantityResponseDto> {
        return cartItemApi.getCartItemsCounts().execute()
    }

    override fun postOrders(orderRequestDto: OrderRequestDto): Response<Unit> {
        return orderApi.postOrders(orderRequestDto = orderRequestDto).execute()
    }
}
