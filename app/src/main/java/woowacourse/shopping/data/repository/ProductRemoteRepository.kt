package woowacourse.shopping.data.repository

import retrofit2.Call
import retrofit2.Response
import woowacourse.shopping.data.retrofit.ProductApi
import woowacourse.shopping.data.retrofit.RetrofitGenerator
import woowacourse.shopping.domain.model.ProductWithCartInfo
import woowacourse.shopping.domain.model.ProductsWithCartItemDTO
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.ServerStoreRespository

class ProductRemoteRepository(
    serverRepository: ServerStoreRespository,
    private val failureCallback: (String?) -> Unit,
) : ProductRepository {
    private val retrofitService =
        RetrofitGenerator.create(serverRepository.getServerUrl(), ProductApi::class.java)

    override fun getProductsByRange(
        lastId: Int,
        pageItemCount: Int,
        callback: (ProductsWithCartItemDTO) -> Unit,
    ) {
        retrofitService.requestProductsByRange(lastId, pageItemCount)
            .enqueue(object : retrofit2.Callback<ProductsWithCartItemDTO> {
                override fun onResponse(
                    call: Call<ProductsWithCartItemDTO>,
                    response: Response<ProductsWithCartItemDTO>,
                ) {
                    if (!response.isSuccessful) {
                        onFailure(call, Throwable(SERVER_ERROR_MESSAGE))
                        return
                    }
                    response.body()?.let {
                        callback(it)
                    }
                }

                override fun onFailure(call: Call<ProductsWithCartItemDTO>, t: Throwable) {
                    failureCallback(t.message)
                }
            })
    }

    override fun getProductById(id: Int, callback: (ProductWithCartInfo) -> Unit) {
        retrofitService.requestProductById(id)
            .enqueue(object : retrofit2.Callback<ProductWithCartInfo> {
                override fun onResponse(
                    call: Call<ProductWithCartInfo>,
                    response: Response<ProductWithCartInfo>,
                ) {
                    if (!response.isSuccessful) {
                        onFailure(call, Throwable(SERVER_ERROR_MESSAGE))
                        return
                    }
                    response.body()?.let { productWithCartInfo ->
                        callback(productWithCartInfo)
                    }
                }

                override fun onFailure(call: Call<ProductWithCartInfo>, t: Throwable) {
                    failureCallback(t.message)
                }
            })
    }

    companion object {
        private const val SERVER_ERROR_MESSAGE = "서버와의 통신이 원활하지 않습니다."
    }
}
