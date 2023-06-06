package woowacourse.shopping.data.datasource.remote.producdetail

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.datasource.remote.retrofit.RetrofitClient
import woowacourse.shopping.data.remote.request.ProductDTO

class ProductDetailRemoteSourceImpl : ProductDetailRemoteSource {

    override fun getById(id: Long, callback: (Result<ProductDTO>) -> Unit) {
        RetrofitClient.getInstance().productDataService.getProductById(id).enqueue(
            object : Callback<ProductDTO> {
                override fun onResponse(
                    call: Call<ProductDTO>,
                    response: Response<ProductDTO>,
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

                override fun onFailure(call: Call<ProductDTO>, t: Throwable) {
                    throw t
                }
            },
        )
    }
}
