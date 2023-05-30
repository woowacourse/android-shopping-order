package woowacourse.shopping.data.product

import com.example.domain.Product
import com.example.domain.repository.ProductRepository
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProductRepositoryImpl(
    url: String,
    port: String = "8080",
) : ProductRepository {

    private val baseUrl = "$url:$port"
    private val retrofitProductService: RetrofitProductService = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RetrofitProductService::class.java)

    override fun requestFetchAllProducts(
        onSuccess: (List<Product>) -> Unit,
        onFailure: () -> Unit
    ) {
        retrofitProductService.requestFetchAllProducts()
            .enqueue(object : retrofit2.Callback<List<Product>> {
                override fun onResponse(
                    call: Call<List<Product>>,
                    response: retrofit2.Response<List<Product>>
                ) {
                    val result = response.body() ?: emptyList()
                    if (400 <= response.code()) return onFailure()
                    onSuccess(result)
                }

                override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                    onFailure()
                }
            })
    }

    override fun requestFetchProductById(
        id: Long,
        onSuccess: (product: Product?) -> Unit,
        onFailure: () -> Unit
    ) {
        retrofitProductService.requestFetchProductById(id)
            .enqueue(object : retrofit2.Callback<Product> {
                override fun onResponse(
                    call: Call<Product>,
                    response: retrofit2.Response<Product>
                ) {
                    val result: Product? = response.body()
                    if (response.isSuccessful) onSuccess(result)
                    if (400 <= response.code()) return onFailure()
                    onSuccess(result)
                }

                override fun onFailure(call: Call<Product>, t: Throwable) {
                    onFailure()
                }
            })
    }
}
