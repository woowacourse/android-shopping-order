package woowacourse.shopping.data.repository

import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.retrofit.RetrofitService
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductsWithCartItemDTO
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRemoteRepository(baseUrl: String) : ProductRepository {
    private val retrofitService = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RetrofitService::class.java)
    // "Authorization", "Basic ZG9vbHlAZG9vbHkuY29tOjEyMzQ="
//    override fun getAll(callBack: (List<Product>) -> Unit) {
//        executeRequest(request, callBack)
//    }
//
//    override fun getProduct(id: Int, callBack: (Product?) -> Unit) {
//        fun callBackWrapper(products: List<Product>) {
//            callBack(products.find { it.id == id })
//        }
//        executeRequest(request, ::callBackWrapper)
//    }

    override fun getProductsByRange(lastId: Int, pageItemCount: Int, callBack: (ProductsWithCartItemDTO) -> Unit) {
        retrofitService.requestProductsByRange(lastId, pageItemCount)
            .enqueue(object : retrofit2.Callback<ProductsWithCartItemDTO> {
                override fun onResponse(call: Call<ProductsWithCartItemDTO>, response: Response<ProductsWithCartItemDTO>) {
                    response.body()?.let {
                        callBack(it)
                    }
                }

                override fun onFailure(call: Call<ProductsWithCartItemDTO>, t: Throwable) {
                    throw t
                }
            })
    }

    override fun getProductsById(ids: List<Int>, callback: (List<Product>) -> Unit) {
        retrofitService.requestProducts().enqueue(object : retrofit2.Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                response.body()?.let { products ->
                    callback(ids.map { id -> products.first { product -> product.id == id } })
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                throw t
            }
        })
    }

//    private fun executeRequest(request: Request, callBack: (List<Product>) -> Unit) {
//        var responseBody: String?
//        client.newCall(request).enqueue(
//            object : Callback {
//                override fun onFailure(call: Call, e: IOException) {
//                    throw java.lang.RuntimeException("Request Failed", e)
//                }
//
//                override fun onResponse(call: Call, response: Response) {
//                    responseBody = response.body?.string()
//                    callBack(parseResponse(responseBody))
//                    response.close()
//                }
//            },
//        )
//    }
}
