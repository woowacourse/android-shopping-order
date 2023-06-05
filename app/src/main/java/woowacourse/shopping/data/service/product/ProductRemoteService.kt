package woowacourse.shopping.data.service.product

import com.example.domain.model.Product
import woowacourse.shopping.data.dto.response.ProductDto
import woowacourse.shopping.data.service.RetrofitApiGenerator
import woowacourse.shopping.mapper.toDomain

class ProductRemoteService {
    fun request(
        onSuccess: (List<Product>) -> Unit,
        onFailure: () -> Unit,
    ) {
        RetrofitApiGenerator.productService.request()
            .enqueue(object : retrofit2.Callback<List<ProductDto>> {
                override fun onResponse(
                    call: retrofit2.Call<List<ProductDto>>,
                    response: retrofit2.Response<List<ProductDto>>,
                ) {
                    if (response.isSuccessful) {
                        val result: List<ProductDto>? = response.body()
                        val products = result?.map { it.toDomain() } ?: emptyList()
                        onSuccess(products)
                    }
                }

                override fun onFailure(call: retrofit2.Call<List<ProductDto>>, t: Throwable) {
                    onFailure()
                }
            })
    }

    fun requestProduct(
        productId: Long,
        onSuccess: (Product) -> Unit,
        onFailure: () -> Unit,
    ) {
        RetrofitApiGenerator.productService.requestProduct(productId)
            .enqueue(object : retrofit2.Callback<ProductDto> {
                override fun onResponse(
                    call: retrofit2.Call<ProductDto>,
                    response: retrofit2.Response<ProductDto>,
                ) {
                    if (response.isSuccessful) {
                        val result: ProductDto? = response.body()
                        val product = result?.toDomain() ?: return onFailure()
                        onSuccess(product)
                    }
                }

                override fun onFailure(call: retrofit2.Call<ProductDto>, t: Throwable) {
                    onFailure()
                }
            })
    }
}
