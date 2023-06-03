package woowacourse.shopping.data.repository

import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.retrofit.ProductApi
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductWithCartInfo
import woowacourse.shopping.domain.model.ProductsWithCartItemDTO
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.ServerStoreRespository

class ProductRemoteRepository(serverRepository: ServerStoreRespository, private val failureCallback: (String?) -> Unit) : ProductRepository {
    private val retrofitService = Retrofit.Builder()
        .baseUrl(serverRepository.getServerUrl())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ProductApi::class.java)

    override fun getProductsByRange(lastId: Int, pageItemCount: Int, callback: (ProductsWithCartItemDTO) -> Unit) {
        retrofitService.requestProductsByRange(lastId, pageItemCount)
            .enqueue(object : retrofit2.Callback<ProductsWithCartItemDTO> {
                override fun onResponse(call: Call<ProductsWithCartItemDTO>, response: Response<ProductsWithCartItemDTO>) {
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

    override fun getProductsById(ids: List<Int>, callback: (List<Product>) -> Unit) {
        retrofitService.requestProducts().enqueue(object : retrofit2.Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (!response.isSuccessful) {
                    onFailure(call, Throwable(SERVER_ERROR_MESSAGE))
                    return
                }
                response.body()?.let { products ->
                    callback(ids.map { id -> products.first { product -> product.id == id } })
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                failureCallback(t.message)
            }
        })
    }

    override fun getProductById(id: Int, callback: (ProductWithCartInfo) -> Unit) {
        retrofitService.requestProductById(id).enqueue(object : retrofit2.Callback<ProductWithCartInfo> {
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
