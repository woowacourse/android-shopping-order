package woowacourse.shopping.retrofit.repository

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.retrofit.RetrofitInterface
import woowacourse.shopping.retrofit.dto.ProductResponse

class RetrofitRepository(
    private val apiService: RetrofitInterface,
) {
    fun requestProduct(
        page: Int,
        size: Int,
        sort: List<String>,
        category: String? = null,
    ) {
        apiService.requestProducts(page = page, size = size, sort = sort, category = category).enqueue(
            object : Callback<ProductResponse> {
                override fun onResponse(
                    call: Call<ProductResponse>,
                    response: Response<ProductResponse>,
                ) {
                    if (response.isSuccessful) {
                        val body = response.body() ?: return
                        println("body : $body")
                    }
                    else if (response.code() == 404){
                        Log.e("RetrofitRepository", "getProduct: 404 error")
                    }
                    else if (response.code() == 500){
                        Log.e("RetrofitRepository", "getProduct: 500 error")
                    }
                    else {
                        Log.e("RetrofitRepository", "getProduct: Exception error")
                    }
                }

                override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                    println("error : $t")
                }
            }
        )
    }

}
