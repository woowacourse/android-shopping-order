package woowacourse.shopping.data.remoteDataSourceImpl

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.remoteDataSource.ProductRemoteDataSource
import woowacourse.shopping.model.Product
import woowacourse.shopping.utils.RetrofitUtil

class ProductRemoteDataSourceImpl : ProductRemoteDataSource {

    override fun getAll(callback: (Result<List<Product>>) -> Unit) {
        RetrofitUtil.retrofitProductService.getProducts().enqueue(
            object : Callback<List<Product>> {
                override fun onResponse(
                    call: Call<List<Product>>,
                    response: Response<List<Product>>
                ) {
                    if (response.isSuccessful) {
                        callback(Result.success(response.body() ?: throw Throwable("Not Found")))
                    } else {
                        callback(Result.failure(Throwable("Not Found")))
                    }
                }

                override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                    callback(Result.failure(t))
                }
            }
        )
    }

    override fun getNext(count: Int, callback: (Result<List<Product>>) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun insert(product: Product, callback: (Result<Int>) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun findById(id: Int, callback: (Result<Product>) -> Unit) {
        RetrofitUtil.retrofitProductService.getProduct(id).enqueue(
            object : Callback<Product> {
                override fun onResponse(call: Call<Product>, response: Response<Product>) {
                    if (response.isSuccessful) {
                        callback(Result.success(response.body() ?: throw Throwable("Not Found")))
                    } else {
                        callback(Result.failure(Throwable("Not Found")))
                    }
                }

                override fun onFailure(call: Call<Product>, t: Throwable) {
                    callback(Result.failure(t))
                }
            }
        )
    }
}
