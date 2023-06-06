package woowacourse.shopping.data.repository.impl

import retrofit2.Call
import retrofit2.Response
import woowacourse.shopping.data.remote.ProductApi
import woowacourse.shopping.data.remote.RetrofitGenerator
import woowacourse.shopping.data.remote.dto.ProductWithCartInfoDTO
import woowacourse.shopping.data.remote.dto.ProductsWithCartItemDTO
import woowacourse.shopping.data.remote.dto.toDomain
import woowacourse.shopping.data.remote.result.DataResult
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.data.repository.ServerStoreRespository
import woowacourse.shopping.domain.model.ProductWithCartInfo
import woowacourse.shopping.domain.model.ProductsWithCartItem

class ProductRemoteRepository(
    serverRepository: ServerStoreRespository,
) : ProductRepository {
    private val productService =
        RetrofitGenerator.create(serverRepository.getServerUrl(), ProductApi::class.java)

    override fun getProductsByRange(
        lastId: Int,
        pageItemCount: Int,
        callback: (DataResult<ProductsWithCartItem>) -> Unit
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
                        callback(DataResult.Success(it.toDomain()))
                    }
                }

                override fun onFailure(call: Call<ProductsWithCartItemDTO>, t: Throwable) {
                    callback(DataResult.Failure)
                }
            })
    }

    override fun getProductById(id: Int, callback: (DataResult<ProductWithCartInfo>) -> Unit) {
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
                        callback(DataResult.Success(it.toDomain()))
                    }
                }

                override fun onFailure(call: Call<ProductWithCartInfoDTO>, t: Throwable) {
                    callback(DataResult.Failure)
                }
            })
    }
}
