package woowacourse.shopping.data.respository.product.source.remote

import android.util.Log
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import woowacourse.shopping.data.mapper.toModel
import woowacourse.shopping.data.model.ProductEntity
import woowacourse.shopping.data.model.Server
import woowacourse.shopping.data.respository.product.service.ProductService
import woowacouse.shopping.model.product.Product

class ProductRemoteDataSourceImpl(
    url: Server.Url,
) : ProductRemoteDataSource {
    private val retrofit = Retrofit.Builder()
        .baseUrl(url.value)
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(ProductService::class.java)

    override fun requestDatas(
        onFailure: () -> Unit,
        onSuccess: (products: List<Product>) -> Unit,
    ) {
        retrofit.requestDatas().enqueue(object : retrofit2.Callback<List<ProductEntity>> {
            override fun onResponse(
                call: retrofit2.Call<List<ProductEntity>>,
                response: retrofit2.Response<List<ProductEntity>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { products ->
                        onSuccess(products.map { it.toModel() })
                    } ?: onFailure()
                } else {
                    onFailure()
                }
            }

            override fun onFailure(call: retrofit2.Call<List<ProductEntity>>, t: Throwable) {
                Log.e("Request Failed", t.toString())
            }
        })
    }

    override fun requestData(
        productId: Long,
        onFailure: () -> Unit,
        onSuccess: (products: Product) -> Unit,
    ) {
        retrofit.requestData(productId).enqueue(object : retrofit2.Callback<ProductEntity> {
            override fun onResponse(
                call: retrofit2.Call<ProductEntity>,
                response: retrofit2.Response<ProductEntity>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { product ->
                        onSuccess(product.toModel())
                    } ?: onFailure()
                } else {
                    onFailure()
                }
            }

            override fun onFailure(call: retrofit2.Call<ProductEntity>, t: Throwable) {
                Log.e("Request Failed", t.toString())
            }
        })
    }
}
