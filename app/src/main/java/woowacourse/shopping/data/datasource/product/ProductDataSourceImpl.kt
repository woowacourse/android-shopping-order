package woowacourse.shopping.data.datasource.product

import android.util.Log
import retrofit2.Call
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.data.dto.ProductDto
import woowacourse.shopping.data.util.retrofit.RetrofitUtil.getProductByRetrofit

class ProductDataSourceImpl : ProductDataSource {
    private val baseUrl: String = ShoppingApplication.pref.getBaseUrl().toString()
    private val retrofitService = getProductByRetrofit(baseUrl)

    override fun requestProducts(): Call<List<ProductDto>> {
        val call = retrofitService.requestProducts()

        call.enqueue(object : retrofit2.Callback<List<ProductDto>> {
            override fun onResponse(
                call: Call<List<ProductDto>>,
                response: retrofit2.Response<List<ProductDto>>,
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    Log.d("test", "retrofit result: $result")
                } else {
                    Log.d("test", "retrofit 실패")
                }
            }

            override fun onFailure(call: Call<List<ProductDto>>, t: Throwable) {
                Log.d("test", "retrofit 실패: ${t.message}")
            }
        })
        return call
    }

    override fun requestProductById(productId: String): Call<ProductDto?> {
        val call = retrofitService.requestProductById(productId)

        call.enqueue(object : retrofit2.Callback<ProductDto?> {
            override fun onResponse(
                call: Call<ProductDto?>,
                response: retrofit2.Response<ProductDto?>,
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    Log.d("test", "retrofit requestProductById result: $result")
                } else {
                    Log.d("test", "retrofit 실패")
                }
            }

            override fun onFailure(call: Call<ProductDto?>, t: Throwable) {
                Log.d("test", "retrofit 실패: ${t.message}")
            }
        })
        return call
    }

    override fun insertProduct(product: ProductDto): Call<ProductDto> {
        val call = retrofitService.insertProduct(product)

        call.enqueue(object : retrofit2.Callback<ProductDto> {
            override fun onResponse(
                call: Call<ProductDto>,
                response: retrofit2.Response<ProductDto>,
            ) {
                if (response.isSuccessful) {
                    Log.d("test", "retrofit requestProductById result: $response")
                } else {
                    Log.d("test", "retrofit 실패")
                }
            }

            override fun onFailure(call: Call<ProductDto>, t: Throwable) {
                Log.d("test", "retrofit 실패: ${t.message}")
            }
        })
        return call
    }

    override fun updateProduct(productId: String, product: ProductDto): Call<ProductDto> {
        val call = retrofitService.updateProduct(productId, product)

        call.enqueue(object : retrofit2.Callback<ProductDto> {
            override fun onResponse(
                call: Call<ProductDto>,
                response: retrofit2.Response<ProductDto>,
            ) {
                if (response.isSuccessful) {
                    Log.d("test", "retrofit requestProductById result: $response")
                } else {
                    Log.d("test", "retrofit 실패")
                }
            }

            override fun onFailure(call: Call<ProductDto>, t: Throwable) {
                Log.d("test", "retrofit 실패: ${t.message}")
            }
        })
        return call
    }

    override fun deleteProduct(productId: String): Call<ProductDto> {
        val call = retrofitService.deleteProduct(productId)

        call.enqueue(object : retrofit2.Callback<ProductDto> {
            override fun onResponse(
                call: Call<ProductDto>,
                response: retrofit2.Response<ProductDto>,
            ) {
                if (response.isSuccessful) {
                    Log.d("test", "retrofit requestProductById result: $response")
                } else {
                    Log.d("test", "retrofit 실패")
                }
            }

            override fun onFailure(call: Call<ProductDto>, t: Throwable) {
                Log.d("test", "retrofit 실패: ${t.message}")
            }
        })
        return call
    }
}
