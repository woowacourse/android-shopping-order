package woowacourse.shopping.data.repository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.api.CartApi
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.data.model.request.CartItemQuantityRequest
import woowacourse.shopping.data.model.request.CartItemRequest
import woowacourse.shopping.data.model.response.CartItemsQuantityResponse
import woowacourse.shopping.data.model.response.CartItemsResponse
import woowacourse.shopping.domain.model.Page
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Product.Companion.EMPTY_PRODUCT
import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.repository.CartRepository

class CartRepository(
    private val api: CartApi,
) : CartRepository {
    override fun fetchCartProductDetail(
        productId: Long,
        callback: (Result<Product?>) -> Unit,
    ) {
        // 임시 로직: 실제 API 없음
        callback(Result.success(EMPTY_PRODUCT))
    }

    override fun fetchCartProducts(
        page: Int,
        size: Int,
        callback: (Result<Products>) -> Unit,
    ) {
        api.getCartItems(page, size).enqueue(
            object : Callback<CartItemsResponse> {
                override fun onResponse(
                    call: Call<CartItemsResponse>,
                    response: Response<CartItemsResponse>,
                ) {
                    val body = response.body()
                    val items = body?.content?.map { it.toDomain() } ?: emptyList()
                    val pageInfo = Page(page, body?.first ?: true, body?.last ?: true)
                    callback(Result.success(Products(items, pageInfo)))
                }

                override fun onFailure(
                    call: Call<CartItemsResponse>,
                    t: Throwable,
                ) {
                    callback(Result.failure(t))
                }
            },
        )
    }

    override fun fetchAllCartProducts(callback: (Result<Products>) -> Unit) {
        val firstPage = 0
        val maxSize = Int.MAX_VALUE
        fetchCartProducts(firstPage, maxSize, callback)
    }

    override fun fetchCartItemCount(callback: (Result<Int>) -> Unit) {
        api.getCartItemsCount().enqueue(
            object : Callback<CartItemsQuantityResponse> {
                override fun onResponse(
                    call: Call<CartItemsQuantityResponse>,
                    response: Response<CartItemsQuantityResponse>,
                ) {
                    callback(Result.success(response.body()?.quantity ?: 0))
                }

                override fun onFailure(
                    call: Call<CartItemsQuantityResponse>,
                    t: Throwable,
                ) {
                    callback(Result.failure(t))
                }
            },
        )
    }

    override fun addCartProduct(
        productId: Long,
        quantity: Int,
        callback: (Result<Unit>) -> Unit,
    ) {
        val request = CartItemRequest(productId = productId, quantity = quantity)
        api.postCartItem(request).enqueue(
            object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>,
                ) {
                    callback(Result.success(Unit))
                }

                override fun onFailure(
                    call: Call<Unit>,
                    t: Throwable,
                ) {
                    callback(Result.failure(t))
                }
            },
        )
    }

    override fun updateCartProduct(
        cartId: Long,
        quantity: Int,
        callback: (Result<Unit>) -> Unit,
    ) {
        val request = CartItemQuantityRequest(quantity)
        api.patchCartItem(cartId, request).enqueue(
            object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>,
                ) {
                    callback(Result.success(Unit))
                }

                override fun onFailure(
                    call: Call<Unit>,
                    t: Throwable,
                ) {
                    callback(Result.failure(t))
                }
            },
        )
    }

    override fun deleteCartProduct(
        cartId: Long,
        callback: (Result<Unit>) -> Unit,
    ) {
        api.deleteCartItem(cartId).enqueue(
            object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>,
                ) {
                    callback(Result.success(Unit))
                }

                override fun onFailure(
                    call: Call<Unit>,
                    t: Throwable,
                ) {
                    callback(Result.failure(t))
                }
            },
        )
    }
}
