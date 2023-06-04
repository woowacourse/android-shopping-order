package woowacourse.shopping.data.product

import com.example.domain.Product
import com.example.domain.repository.ProductRepository
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.product.model.ProductDto
import woowacourse.shopping.data.product.model.ProductsResponse
import woowacourse.shopping.data.product.model.toDomain

class ProductRemoteRepository(
    url: String,
    port: String = "8080",
) : ProductRepository {

    private val baseUrl = "$url:$port"
    private val retrofitProductService: RetrofitProductService = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RetrofitProductService::class.java)

    override fun requestFetchProductsUnit(
        unitSize: Int,
        page: Int,
        onSuccess: (List<Product>) -> Unit,
        onFailure: () -> Unit
    ) {
        retrofitProductService.requestFetchProductsUnit(
            unitSize = unitSize,
            page = page
        ).enqueue(object : retrofit2.Callback<ProductsResponse> {
            override fun onResponse(
                call: Call<ProductsResponse>,
                response: retrofit2.Response<ProductsResponse>
            ) {
                val result: ProductsResponse = response.body() ?: return
                val products: List<Product> = result.products.map(ProductDto::toDomain)
                if (400 <= response.code()) return onFailure()
                onSuccess(products)
            }

            override fun onFailure(call: Call<ProductsResponse>, t: Throwable) {
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
