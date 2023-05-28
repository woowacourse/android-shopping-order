package woowacourse.shopping.data.datasource.remote.product

import com.example.domain.model.Product
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.model.ProductEntity
import woowacourse.shopping.data.model.toDomain
import woowacourse.shopping.data.datasource.remote.ServerInfo

class ProductRetrofitService : ProductRemoteDataSource {

    private val productApi = Retrofit.Builder()
        .baseUrl(ServerInfo.currentBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ProductApi::class.java)

    override fun requestProducts(onSuccess: (List<Product>) -> Unit, onFailure: () -> Unit) {
        productApi.requestProducts().enqueue(object : retrofit2.Callback<List<ProductEntity>> {
            override fun onResponse(
                call: Call<List<ProductEntity>>,
                response: Response<List<ProductEntity>>
            ) {
                response.body()?.let {
                    onSuccess(it.map { productEntity ->
                        productEntity.toDomain()
                    })
                }
            }

            override fun onFailure(call: Call<List<ProductEntity>>, t: Throwable) {
                onFailure()
            }
        })
    }

}
