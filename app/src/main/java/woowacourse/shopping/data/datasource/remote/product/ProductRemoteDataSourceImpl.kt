package woowacourse.shopping.data.datasource.remote.product

import woowacourse.shopping.data.datasource.remote.retrofit.RetrofitClient
import woowacourse.shopping.data.remote.request.ProductDTO

class ProductRemoteDataSourceImpl : ProductRemoteDataSource {

    override fun getSubListProducts(
        limit: Int,
        scrollCount: Int,
        callback: (Result<List<ProductDTO>>) -> Unit,
    ) {
        RetrofitClient.getInstance().productDataService.getProducts(limit, scrollCount)
            .enqueue(object : retrofit2.Callback<List<ProductDTO>> {
                override fun onResponse(
                    call: retrofit2.Call<List<ProductDTO>>,
                    response: retrofit2.Response<List<ProductDTO>>,
                ) {
                    if (response.isSuccessful) {
                        callback(
                            Result.success(
                                response.body() ?: throw IllegalArgumentException(),
                            ),
                        )
                    } else {
                        callback(Result.failure(Throwable(response.message())))
                    }
                }

                override fun onFailure(call: retrofit2.Call<List<ProductDTO>>, t: Throwable) {
                    throw t
                }
            })
    }
}
