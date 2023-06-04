package woowacourse.shopping.data.datasource.product

import android.util.Log
import retrofit2.Call
import retrofit2.Response
import woowacourse.shopping.ShoppingApplication.Companion.pref
import woowacourse.shopping.data.dto.ProductDto
import woowacourse.shopping.data.dto.ProductsDto
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.data.util.retrofit.RetrofitUtil.getProductByRetrofit
import woowacourse.shopping.domain.model.Product

class ProductDataSourceImpl : ProductDataSource {
    private val baseUrl: String = pref.getBaseUrl().toString()
    private val retrofitService = getProductByRetrofit(baseUrl)

    override fun requestProducts(
        onSuccess: (List<Product>) -> Unit,
        onFailure: () -> Unit,
    ) {
        val call = retrofitService.requestProducts(1, 20)
        call.enqueue(object : retrofit2.Callback<ProductsDto> {
            override fun onResponse(
                call: Call<ProductsDto>,
                response: Response<ProductsDto>,
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    val products = result?.products?.map { it.toDomain() }
                    products?.let { onSuccess(it) }
                    Log.d("test", "retrofit result: $result")
                } else {
                    Log.d("test", "retrofit 실패")
                }
            }

            override fun onFailure(call: Call<ProductsDto>, t: Throwable) {
                Log.d("test", "retrofit 실패: ${t.message}")
            }
        })
    }

    override fun requestProductById(
        productId: String,
        onSuccess: (Product?) -> Unit,
        onFailure: () -> Unit,
    ) {
        val call = retrofitService.requestProductById(productId)
        call.enqueue(object : retrofit2.Callback<ProductDto?> {
            override fun onResponse(
                call: Call<ProductDto?>,
                response: Response<ProductDto?>,
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    Log.d("test", "retrofit requestProductById result: $result")
                    if (result != null) {
                        onSuccess(result.toDomain())
                    }
                } else {
                    Log.d("test", "retrofit 실패")
                }
            }

            override fun onFailure(call: Call<ProductDto?>, t: Throwable) {
                Log.d("test", "retrofit 실패: ${t.message}")
                onFailure()
            }
        })
    }

    override fun insertProduct(
        product: ProductDto,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) {
        val call = retrofitService.insertProduct(product)

        call.enqueue(object : retrofit2.Callback<ProductDto> {
            override fun onResponse(
                call: Call<ProductDto>,
                response: Response<ProductDto>,
            ) {
                if (response.isSuccessful) {
                    Log.d("test", "retrofit requestProductById result: $response")
                    onSuccess()
                } else {
                    Log.d("test", "retrofit 실패")
                }
            }

            override fun onFailure(call: Call<ProductDto>, t: Throwable) {
                Log.d("test", "retrofit 실패: ${t.message}")
                onFailure()
            }
        })
    }

    override fun updateProduct(
        productId: String,
        product: ProductDto,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) {
        val call = retrofitService.updateProduct(productId, product)
        call.enqueue(object : retrofit2.Callback<ProductDto> {
            override fun onResponse(
                call: Call<ProductDto>,
                response: Response<ProductDto>,
            ) {
                if (response.isSuccessful) {
                    Log.d("test", "retrofit requestProductById result: $response")
                    onSuccess()
                } else {
                    Log.d("test", "retrofit 실패")
                }
            }

            override fun onFailure(call: Call<ProductDto>, t: Throwable) {
                Log.d("test", "retrofit 실패: ${t.message}")
                onFailure()
            }
        })
    }

    override fun deleteProduct(
        productId: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) {
        val call = retrofitService.deleteProduct(productId)
        call.enqueue(object : retrofit2.Callback<ProductDto> {
            override fun onResponse(
                call: Call<ProductDto>,
                response: Response<ProductDto>,
            ) {
                if (response.isSuccessful) {
                    Log.d("test", "retrofit requestProductById result: $response")
                    onSuccess()
                } else {
                    Log.d("test", "retrofit 실패")
                }
            }

            override fun onFailure(call: Call<ProductDto>, t: Throwable) {
                Log.d("test", "retrofit 실패: ${t.message}")
                onFailure()
            }
        })
    }
}
