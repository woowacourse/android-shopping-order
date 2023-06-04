package woowacourse.shopping.data.dataSource

import woowacourse.shopping.data.service.RetrofitProductService
import woowacourse.shopping.data.service.RetrofitUtil
import woowacourse.shopping.model.Product

class RemoteProductDataSource(
    private val service: RetrofitProductService = RetrofitUtil.retrofitProductService,
) : ProductDataSource {

    override fun getAll(callback: (List<Product>?) -> Unit) {
        service.getProducts().enqueue(
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

    override fun findById(id: Int, callback: (Product?) -> Unit) {
        service.getProduct(id).enqueue(
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
