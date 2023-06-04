package woowacourse.shopping.remoteService

import woowacourse.shopping.model.Product
import woowacourse.shopping.service.RetrofitUtil

class RemoteProductService {

    fun getAll(callback: (List<Product>?) -> Unit) {
        RetrofitUtil.retrofitProductService.getProducts().enqueue(
            object : retrofit2.Callback<List<Product>> {
                override fun onResponse(
                    call: retrofit2.Call<List<Product>>,
                    response: retrofit2.Response<List<Product>>,
                ) {
                    callback(response.body())
                }

                override fun onFailure(call: retrofit2.Call<List<Product>>, t: Throwable) {
                    callback(null)
                }
            },
        )
    }

    fun findById(id: Int, callback: (Product?) -> Unit) {
        RetrofitUtil.retrofitProductService.getProduct(id).enqueue(
            object : retrofit2.Callback<Product> {
                override fun onResponse(
                    call: retrofit2.Call<Product>,
                    response: retrofit2.Response<Product>,
                ) {
                    callback(response.body())
                }

                override fun onFailure(call: retrofit2.Call<Product>, t: Throwable) {
                    callback(null)
                }
            },
        )
    }
}
