package woowacourse.shopping.data.remote.product

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.ApiClient
import woowacourse.shopping.data.common.BaseResponse

object ProductRemoteDataSource : ProductDataSource {
    private val productService = ApiClient.client
        .create(ProductService::class.java)

    override fun getProductById(
        id: Int,
        onSuccess: (ProductDataModel) -> Unit,
        onFailure: () -> Unit
    ) {
        productService.getProductById(id).enqueue(object :
                Callback<BaseResponse<ProductDataModel>> {
                override fun onResponse(
                    call: Call<BaseResponse<ProductDataModel>>,
                    response: Response<BaseResponse<ProductDataModel>>,
                ) {
                    val responseBody = response.body()
                    if (responseBody == null) {
                        onFailure()
                        return
                    }
                    onSuccess(responseBody.result)
                }

                override fun onFailure(call: Call<BaseResponse<ProductDataModel>>, t: Throwable) {
                    Log.d("HttpError", t.message.toString())
                    onFailure()
                }
            })
    }

    override fun getAllProducts(
        onSuccess: (List<ProductDataModel>) -> Unit,
        onFailure: () -> Unit
    ) {
        productService.getAllProducts()
            .enqueue(object : Callback<BaseResponse<List<ProductDataModel>>> {
                override fun onResponse(
                    call: Call<BaseResponse<List<ProductDataModel>>>,
                    response: Response<BaseResponse<List<ProductDataModel>>>,
                ) {
                    val allProducts = response.body()?.result
                    if (allProducts == null) {
                        onSuccess(emptyList())
                        return
                    }
                    onSuccess(allProducts)
                }

                override fun onFailure(
                    call: Call<BaseResponse<List<ProductDataModel>>>,
                    t: Throwable
                ) {
                    Log.d("HttpError", t.message.toString())
                    onFailure()
                }
            })
    }
}
