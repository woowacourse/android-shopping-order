package woowacourse.shopping.data.repository.impl

import retrofit2.Call
import retrofit2.Response
import woowacourse.shopping.data.remote.ProductApi
import woowacourse.shopping.data.remote.RetrofitGenerator
import woowacourse.shopping.data.remote.dto.ProductsWithCartItemDTO
import woowacourse.shopping.data.remote.result.DataResult
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.data.repository.ServerStoreRespository
import woowacourse.shopping.domain.model.ProductWithCartInfo

class ProductRemoteRepository(
    serverRepository: ServerStoreRespository,
) : ProductRepository {
    private val retrofitService =
        RetrofitGenerator.create(serverRepository.getServerUrl(), ProductApi::class.java)

    override fun getProductsByRange(
        lastId: Int,
        pageItemCount: Int,
        callback: (DataResult<ProductsWithCartItemDTO>) -> Unit
    ) {
        retrofitService.requestProductsByRange(lastId, pageItemCount)
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
                        callback(DataResult.Success(it))
                    }
                }

                override fun onFailure(call: Call<ProductsWithCartItemDTO>, t: Throwable) {
                    callback(DataResult.Failure)
                }
            })
    }

    override fun getProductById(id: Int, callback: (DataResult<ProductWithCartInfo>) -> Unit) {
        retrofitService.requestProductById(id)
            .enqueue(object : retrofit2.Callback<ProductWithCartInfo> {
                override fun onResponse(
                    call: Call<ProductWithCartInfo>,
                    response: Response<ProductWithCartInfo>,
                ) {
                    if (!response.isSuccessful) {
                        callback(DataResult.NotSuccessfulError)
                        return
                    }
                    response.body()?.let {
                        callback(DataResult.Success(it))
                    }
                }

                override fun onFailure(call: Call<ProductWithCartInfo>, t: Throwable) {
                    callback(DataResult.Failure)
                }
            })
    }
}
