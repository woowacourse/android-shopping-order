package woowacourse.shopping.data.service.product

import com.example.domain.model.CustomError
import com.example.domain.model.Product
import woowacourse.shopping.data.dto.response.ErrorDto
import woowacourse.shopping.data.dto.response.ProductDto
import woowacourse.shopping.data.service.RetrofitApiGenerator
import woowacourse.shopping.mapper.toDomain

class ProductRemoteService {
    fun request(
        onSuccess: (List<Product>) -> Unit,
        onFailure: (CustomError) -> Unit,
    ) {
        RetrofitApiGenerator.productService.request()
            .enqueue(object : retrofit2.Callback<List<ProductDto>> {
                override fun onResponse(
                    call: retrofit2.Call<List<ProductDto>>,
                    response: retrofit2.Response<List<ProductDto>>,
                ) {
                    if (!response.isSuccessful) {
                        onFailure(CustomError(ErrorDto.mapToErrorDto(response.errorBody()).message))
                    }
                    if (response.isSuccessful) {
                        val result: List<ProductDto>? = response.body()
                        val products = result?.map { it.toDomain() } ?: emptyList()
                        onSuccess(products)
                    }
                }

                override fun onFailure(call: retrofit2.Call<List<ProductDto>>, t: Throwable) {
                    onFailure(CustomError())
                }
            })
    }

    fun requestProduct(
        productId: Long,
        onSuccess: (Product) -> Unit,
        onFailure: (CustomError) -> Unit,
    ) {
        RetrofitApiGenerator.productService.requestProduct(productId)
            .enqueue(object : retrofit2.Callback<ProductDto> {
                override fun onResponse(
                    call: retrofit2.Call<ProductDto>,
                    response: retrofit2.Response<ProductDto>,
                ) {
                    if (!response.isSuccessful) {
                        onFailure(CustomError(ErrorDto.mapToErrorDto(response.errorBody()).message))
                    }
                    if (response.isSuccessful) {
                        val product = response.body()?.toDomain() ?: return onFailure(CustomError("응답 바디 매핑 실패"))
                        onSuccess(product)
                    }
                }

                override fun onFailure(call: retrofit2.Call<ProductDto>, t: Throwable) {
                    onFailure(CustomError())
                }
            })
    }
}
