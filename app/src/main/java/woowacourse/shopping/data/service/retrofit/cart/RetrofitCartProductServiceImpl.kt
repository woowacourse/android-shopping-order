package woowacourse.shopping.data.service.retrofit.cart

import android.util.Log
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.ShoppingApplication.Companion.pref
import woowacourse.shopping.data.dto.CartProductDto
import woowacourse.shopping.data.service.retrofit.product.RetrofitProductServiceImpl.NullOnEmptyConverterFactory
import woowacourse.shopping.util.RetrofitUtil.okHttpClient

class RetrofitCartProductServiceImpl : RetrofitCartProductService {
    private val baseUrl: String = pref.getBaseUrl().toString()
    private val retrofitService = getRetrofit()

    override fun requestCartProducts(token: String): Call<List<CartProductDto>> {
        val call = retrofitService.requestCartProducts(token)

        call.enqueue(object : retrofit2.Callback<List<CartProductDto>> {
            override fun onResponse(
                call: Call<List<CartProductDto>>,
                response: retrofit2.Response<List<CartProductDto>>,
            ) {
                println("${response.code()}")
                if (response.isSuccessful) {
                    val result = response.body()
                    Log.d("test", "retrofit result: $result")
                } else {
                    Log.d("test", "retrofit 실패 ")
                }
            }

            override fun onFailure(call: Call<List<CartProductDto>>, t: Throwable) {
                Log.d("test", "retrofit 실패: ${t.message}")
            }
        })
        return call
    }

    override fun addCartProductByProductId(token: String, productId: String): Call<Int> {
        val call = retrofitService.addCartProductByProductId(token, productId)

        call.enqueue(object : retrofit2.Callback<Int> {
            override fun onResponse(
                call: Call<Int>,
                response: retrofit2.Response<Int>,
            ) {
                println("${response.code()}")
                if (response.isSuccessful) {
                    Log.d("test", "retrofit result: 성공")
                } else {
                    Log.d("test", "retrofit 실패 ")
                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                Log.d("test", "retrofit 실패: ${t.message}")
            }
        })
        return call
    }

    override fun updateCartProductCountById(
        token: String,
        cartItemId: String,
        quantity: Int,
    ): Call<Void> {
        val call = retrofitService.updateCartProductCountById(token, cartItemId, quantity)

        call.enqueue(object : retrofit2.Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: retrofit2.Response<Void>,
            ) {
                println("${response.code()}")
                if (response.isSuccessful) {
                    Log.d("test", "retrofit result: 성공")
                } else {
                    Log.d("test", "retrofit 실패 ")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("test", "retrofit 실패: ${t.message}")
            }
        })
        return call
    }

    override fun deleteCartPRoductById(token: String, cartItemId: String): Call<CartProductDto> {
        val call = retrofitService.deleteCartPRoductById(token, cartItemId)

        call.enqueue(object : retrofit2.Callback<CartProductDto> {
            override fun onResponse(
                call: Call<CartProductDto>,
                response: retrofit2.Response<CartProductDto>,
            ) {
                println("${response.code()}")
                if (response.isSuccessful) {
                    Log.d("test", "retrofit result: 성공")
                } else {
                    Log.d("test", "retrofit 실패 ")
                }
            }

            override fun onFailure(call: Call<CartProductDto>, t: Throwable) {
                Log.d("test", "retrofit 실패: ${t.message}")
            }
        })
        return call
    }

//    override fun requestCartProductById(productId: Int): CartProduct? {
//        val cartProducts = requestCartProducts()
//        return cartProducts.find { it.product.id == productId }
//    }

    private fun getRetrofit(): RetrofitCartProductService {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(RetrofitCartProductService::class.java)
    }
}
