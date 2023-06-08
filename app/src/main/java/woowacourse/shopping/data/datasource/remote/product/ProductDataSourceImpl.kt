package woowacourse.shopping.data.datasource.remote.product

import woowacourse.shopping.data.datasource.remote.RetrofitService
import woowacourse.shopping.data.model.product.ProductDto

class ProductDataSourceImpl : ProductRemoteDataSource {

    private val productService = RetrofitService.productService

    override fun loadAll(
        onSuccess: (List<ProductDto>) -> Unit,
        onFailure: () -> Unit
    ) {
        productService.requestProducts().enqueue(object : retrofit2.Callback<List<ProductDto>> {
            override fun onResponse(
                call: retrofit2.Call<List<ProductDto>>,
                response: retrofit2.Response<List<ProductDto>>
            ) {
                if (response.code() >= 400) return onFailure()
                val value = response.body() ?: emptyList()

                onSuccess(value)
            }

            override fun onFailure(call: retrofit2.Call<List<ProductDto>>, t: Throwable) {
                onFailure()
            }
        })
    }
}
