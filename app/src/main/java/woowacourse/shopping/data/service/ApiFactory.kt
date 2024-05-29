package woowacourse.shopping.data.service

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import woowacourse.shopping.BuildConfig
import woowacourse.shopping.data.dto.response.ResponseProductsGetDto
import woowacourse.shopping.model.Product

object ApiFactory {
    private val client =
        OkHttpClient.Builder().addInterceptor(DefaultInterceptor("namyunsuk", "password")).build()
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .client(client)
            .build()
    }

    private val productService = retrofit.create(ProductService::class.java)

    fun getProductsByOffset(
        page: Int,
        size: Int,
        success: (List<Product>) -> Unit,
    ) {
        productService.getProductsByOffset(page = page, size = size)
            .enqueue(
                object : Callback<ResponseProductsGetDto> {
                    override fun onResponse(
                        call: Call<ResponseProductsGetDto>,
                        response: Response<ResponseProductsGetDto>,
                    ) {
                        val body = response.body()
                        val products =
                            body?.content?.map {
                                Product(
                                    id = it.id,
                                    imageUrl = it.imageUrl,
                                    name = it.name,
                                    price = it.price,
                                )
                            }
                        products?.let {
                            success(it)
                        }
                    }

                    override fun onFailure(
                        call: Call<ResponseProductsGetDto>,
                        e: Throwable,
                    ) {
                        throw e
                    }
                },
            )
    }

    fun getProductsById(id: Long) = productService.getProductsById(id = id).execute().body()
}
