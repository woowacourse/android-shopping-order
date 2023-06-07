package woowacourse.shopping.data.datasource.impl

import retrofit2.Call
import retrofit2.Response
import woowacourse.shopping.data.datasource.ProductDataSource
import woowacourse.shopping.data.remote.ProductApi
import woowacourse.shopping.data.remote.RetrofitGenerator
import woowacourse.shopping.data.remote.dto.ProductWithCartInfoDTO
import woowacourse.shopping.data.remote.dto.ProductsWithCartItemDTO
import woowacourse.shopping.data.remote.result.DataResult

class ProductRemoteDataSource(url: String) : ProductDataSource {
    private val productService =
        RetrofitGenerator.create(url, ProductApi::class.java)

    override fun getProductsByRange(
        lastId: Int,
        pageItemCount: Int,
        callback: (DataResult<ProductsWithCartItemDTO>) -> Unit,
    ) {
        productService.requestProductsByRange(lastId, pageItemCount)
            .enqueue(object : retrofit2.Callback<ProductsWithCartItemDTO> {
                override fun onResponse(
                    call: Call<ProductsWithCartItemDTO>,
                    response: Response<ProductsWithCartItemDTO>,
                ) {
                    if (!response.isSuccessful) {
                        callback(DataResult.NotSuccessfulError)
                        return
                    }
                    response.body()?.let {
                        if (!it.isNotNull) {
                            callback(DataResult.WrongResponse)
                            return
                        }
                        callback(DataResult.Success(it))
                    }
                }

                override fun onFailure(call: Call<ProductsWithCartItemDTO>, t: Throwable) {
                    callback(DataResult.Failure)
                }
            })
    }

    override fun getProductById(id: Int, callback: (DataResult<ProductWithCartInfoDTO>) -> Unit) {
        productService.requestProductById(id)
            .enqueue(object : retrofit2.Callback<ProductWithCartInfoDTO> {
                override fun onResponse(
                    call: Call<ProductWithCartInfoDTO>,
                    response: Response<ProductWithCartInfoDTO>,
                ) {
                    if (!response.isSuccessful) {
                        callback(DataResult.NotSuccessfulError)
                        return
                    }
                    response.body()?.let {
                        if (!it.isNotNull) {
                            callback(DataResult.WrongResponse)
                            return
                        }
                        callback(DataResult.Success(it))
                    }
                }

                override fun onFailure(call: Call<ProductWithCartInfoDTO>, t: Throwable) {
                    callback(DataResult.Failure)
                }
            })
    }
}
