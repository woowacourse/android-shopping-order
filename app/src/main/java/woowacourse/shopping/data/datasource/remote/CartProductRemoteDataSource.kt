package woowacourse.shopping.data.datasource.remote

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.dto.request.CartProductQuantityRequestDto
import woowacourse.shopping.data.dto.request.CartProductRequestDto
import woowacourse.shopping.data.dto.response.CartProductQuantityResponseDto
import woowacourse.shopping.data.dto.response.CartProductResponseDto
import woowacourse.shopping.data.dto.response.toCartProduct
import woowacourse.shopping.data.model.PagedResult
import woowacourse.shopping.data.service.CartProductApiService
import woowacourse.shopping.domain.model.CartProduct

class CartProductRemoteDataSource(
    private val cartProductService: CartProductApiService,
) {
    fun getPagedProducts(
        page: Int?,
        size: Int?,
        onResult: (Result<PagedResult<CartProduct>>) -> Unit,
    ) {
        cartProductService.getPagedProducts(page = page, size = size).enqueue(
            object : Callback<CartProductResponseDto> {
                override fun onResponse(
                    call: Call<CartProductResponseDto>,
                    response: Response<CartProductResponseDto>,
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { body ->
                            val products = body.content.map { it.toCartProduct() }
                            val hasNext = body.last.not()
                            onResult(Result.success(PagedResult(products, hasNext)))
                        } ?: onResult(Result.success(PagedResult(emptyList(), false)))
                    } else {
                        onResult(Result.failure(Exception("HTTP ${response.code()}: ${response.message()}")))
                    }
                }

                override fun onFailure(
                    call: Call<CartProductResponseDto>,
                    t: Throwable,
                ) {
                    onResult(Result.failure(t))
                }
            },
        )
    }

    fun insert(
        id: Int,
        quantity: Int,
        onResult: (Result<Int>) -> Unit,
    ) {
        cartProductService.insert(body = CartProductRequestDto(id, quantity)).enqueue(
            object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>,
                ) {
                    if (response.code() == SUCCESS_POST) {
                        val cartProductId =
                            response.headers()[HEADER_LOCATION]?.removePrefix(PREFIX_CART_ITEM)?.toInt()
                                ?: throw IllegalArgumentException()
                        onResult(Result.success(cartProductId))
                    } else {
                        onResult(Result.failure(Exception("HTTP ${response.code()}: ${response.message()}")))
                    }
                }

                override fun onFailure(
                    call: Call<Unit>,
                    t: Throwable,
                ) {
                    onResult(Result.failure(t))
                }
            },
        )
    }

    fun delete(
        id: Int,
        onResult: (Result<Unit>) -> Unit,
    ) {
        cartProductService.delete(id = id).enqueue(
            object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>,
                ) {
                    if (response.code() == SUCCESS_DELETE) {
                        onResult(Result.success(Unit))
                    } else {
                        onResult(Result.failure(Exception("HTTP ${response.code()}: ${response.message()}")))
                    }
                }

                override fun onFailure(
                    call: Call<Unit>,
                    t: Throwable,
                ) {
                    onResult(Result.failure(t))
                }
            },
        )
    }

    fun getTotalQuantity(onResult: (Result<Int>) -> Unit) {
        cartProductService.getTotalQuantity().enqueue(
            object : Callback<CartProductQuantityResponseDto> {
                override fun onResponse(
                    call: Call<CartProductQuantityResponseDto>,
                    response: Response<CartProductQuantityResponseDto>,
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { body ->
                            onResult(Result.success(body.quantity))
                        }
                    } else {
                        onResult(Result.failure(Exception("HTTP ${response.code()}: ${response.message()}")))
                    }
                }

                override fun onFailure(
                    call: Call<CartProductQuantityResponseDto>,
                    t: Throwable,
                ) {
                    onResult(Result.failure(t))
                }
            },
        )
    }

    fun updateQuantity(
        id: Int,
        quantity: Int,
        onResult: (Result<Unit>) -> Unit,
    ) {
        cartProductService
            .updateQuantity(id = id, body = CartProductQuantityRequestDto(quantity))
            .enqueue(
                object : Callback<Unit> {
                    override fun onResponse(
                        call: Call<Unit>,
                        response: Response<Unit>,
                    ) {
                        if (response.code() == SUCCESS_PATCH) {
                            onResult(Result.success(Unit))
                        } else {
                            onResult(Result.failure(Exception("HTTP ${response.code()}: ${response.message()}")))
                        }
                    }

                    override fun onFailure(
                        call: Call<Unit>,
                        t: Throwable,
                    ) {
                        onResult(Result.failure(t))
                    }
                },
            )
    }

    companion object {
        private const val SUCCESS_PATCH = 200
        private const val SUCCESS_POST = 201
        private const val SUCCESS_DELETE = 204
        private const val HEADER_LOCATION = "location"
        private const val PREFIX_CART_ITEM = "/cart-items/"
    }
}
