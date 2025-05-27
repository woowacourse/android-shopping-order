package woowacourse.shopping.data.datasource.remote

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
        page: Int,
        size: Int,
        onSuccess: (PagedResult<CartProduct>) -> Unit,
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
                            onSuccess(PagedResult(products, hasNext))
                        } ?: onSuccess(PagedResult(emptyList(), false))
                    }
                }

                override fun onFailure(
                    call: Call<CartProductResponseDto>,
                    t: Throwable,
                ) {
                    println("error : $t")
                }
            },
        )
    }

    fun insert(
        id: Int,
        quantity: Int,
        onSuccess: () -> Unit,
    ) {
        cartProductService.insert(body = CartProductRequestDto(id, quantity)).enqueue(
            object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>,
                ) {
                    if (response.code() == SUCCESS_POST) {
                        onSuccess()
                    }
                }

                override fun onFailure(
                    call: Call<Unit>,
                    t: Throwable,
                ) {
                    println("error : $t")
                }
            },
        )
    }

    fun deleteByProductId(
        id: Int,
        onSuccess: () -> Unit,
    ) {
        cartProductService.deleteByProductId(id = id).enqueue(
            object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>,
                ) {
                    if (response.code() == SUCCESS_DELETE) {
                        onSuccess()
                    }
                }

                override fun onFailure(
                    call: Call<Unit>,
                    t: Throwable,
                ) {
                    println("error : $t")
                }
            },
        )
    }

    fun getTotalQuantity(onSuccess: (Int) -> Unit) {
        cartProductService.getTotalQuantity().enqueue(
            object : Callback<CartProductQuantityResponseDto> {
                override fun onResponse(
                    call: Call<CartProductQuantityResponseDto>,
                    response: Response<CartProductQuantityResponseDto>,
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { body ->
                            onSuccess(body.quantity)
                        }
                    }
                }

                override fun onFailure(
                    call: Call<CartProductQuantityResponseDto>,
                    t: Throwable,
                ) {
                    println("error : $t")
                }
            },
        )
    }

    companion object {
        private const val SUCCESS_POST = 201
        private const val SUCCESS_DELETE = 204
    }
}
