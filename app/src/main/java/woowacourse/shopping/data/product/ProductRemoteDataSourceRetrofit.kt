package woowacourse.shopping.data.product

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import woowacourse.shopping.Storage
import woowacourse.shopping.data.entity.ProductEntity
import woowacourse.shopping.data.entity.mapper.ProductMapper.toDomain
import woowacourse.shopping.data.server.ProductRemoteDataSource
import woowacourse.shopping.data.server.Server
import woowacourse.shopping.domain.Product

class ProductRemoteDataSourceRetrofit : ProductRemoteDataSource {
    private val productService: ProductService = Retrofit.Builder()
        .baseUrl(Server.getUrl(Storage.server))
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(ProductService::class.java)

    override fun getProducts(onSuccess: (List<Product>) -> Unit, onFailure: () -> Unit) {
        productService.requestProducts().enqueue(object : retrofit2.Callback<List<ProductEntity>> {
            override fun onResponse(
                call: Call<List<ProductEntity>>,
                response: Response<List<ProductEntity>>
            ) {
                if(response.isSuccessful) {
                    onSuccess(response.body()?.map { it.toDomain() } ?: emptyList())
                }
                else {
                    onFailure()
                }
            }

            override fun onFailure(call: Call<List<ProductEntity>>, t: Throwable) {
                onFailure()
            }
        })
    }

    override fun getProduct(id: Int, onSuccess: (Product) -> Unit, onFailure: () -> Unit) {
        TODO("Not yet implemented")
    }
}