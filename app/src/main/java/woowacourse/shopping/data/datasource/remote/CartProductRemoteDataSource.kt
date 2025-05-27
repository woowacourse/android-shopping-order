package woowacourse.shopping.data.datasource.remote

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
}
