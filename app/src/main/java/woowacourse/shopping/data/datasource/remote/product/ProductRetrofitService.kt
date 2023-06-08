package woowacourse.shopping.data.datasource.remote.product

import com.example.domain.model.Product
import retrofit2.Call
import retrofit2.Response
import woowacourse.shopping.data.datasource.remote.RetrofitService
import woowacourse.shopping.data.model.ProductDto
import woowacourse.shopping.data.model.toDomain

class ProductRetrofitService : ProductRemoteDataSource {

    private val productService = RetrofitService.productService

    override fun requestProducts(
        onSuccess: (List<Product>) -> Unit,
        onFailure: (t: Throwable) -> Unit
    ) {
        productService.requestProducts().enqueue(object : retrofit2.Callback<List<ProductDto>> {
            override fun onResponse(
                call: Call<List<ProductDto>>,
                response: Response<List<ProductDto>>
            ) {
                response.body()?.let {
                    onSuccess(
                        it.map { productEntity ->
                            productEntity.toDomain()
                        }
                    )
                }
            }

            override fun onFailure(call: Call<List<ProductDto>>, t: Throwable) {
                onFailure(t)
            }
        })
    }
}
