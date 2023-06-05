package woowacourse.shopping.data.dataSource

import woowacourse.shopping.data.dto.ProductDto
import woowacourse.shopping.data.service.RetrofitClient
import woowacourse.shopping.data.service.RetrofitProductService

class RemoteProductDataSource(
    private val service: RetrofitProductService = RetrofitClient.getInstance().retrofitProductService,
) : ProductDataSource {

    override fun getAll(callback: (List<ProductDto>?) -> Unit) {
        service.getProducts().enqueue(
            object : retrofit2.Callback<List<ProductDto>> {
                override fun onResponse(
                    call: retrofit2.Call<List<ProductDto>>,
                    response: retrofit2.Response<List<ProductDto>>,
                ) {
                    callback(response.body())
                }

                override fun onFailure(call: retrofit2.Call<List<ProductDto>>, t: Throwable) {
                    callback(null)
                }
            },
        )
    }

    override fun findById(id: Int, callback: (ProductDto?) -> Unit) {
        service.getProduct(id).enqueue(
            object : retrofit2.Callback<ProductDto> {
                override fun onResponse(
                    call: retrofit2.Call<ProductDto>,
                    response: retrofit2.Response<ProductDto>,
                ) {
                    callback(response.body())
                }

                override fun onFailure(call: retrofit2.Call<ProductDto>, t: Throwable) {
                    callback(null)
                }
            },
        )
    }
}
