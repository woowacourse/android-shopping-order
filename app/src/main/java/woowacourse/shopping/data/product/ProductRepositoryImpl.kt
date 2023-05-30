package woowacourse.shopping.data.product

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.Product
import woowacourse.shopping.Products
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.repository.ProductRepository

class ProductRepositoryImpl constructor(
    private val productRemoteDataSource: ProductRemoteDataSource,
) : ProductRepository {
    override fun findProductById(id: Int, onSuccess: (Product?) -> Unit) {
        productRemoteDataSource.getProductById(id).enqueue(object : Callback<ProductDataModel> {
            override fun onResponse(
                call: Call<ProductDataModel>,
                response: Response<ProductDataModel>,
            ) {
                onSuccess(response.body()?.toDomain())
            }

            override fun onFailure(call: Call<ProductDataModel>, t: Throwable) {
                Log.d("HttpError", t.message.toString())
                throw (t)
            }
        })
    }

    override fun getProductsWithRange(start: Int, size: Int, onSuccess: (List<Product>) -> Unit) {
        productRemoteDataSource.getAllProducts().enqueue(object : Callback<List<ProductDataModel>> {
            override fun onResponse(
                call: Call<List<ProductDataModel>>,
                response: Response<List<ProductDataModel>>,
            ) {
                val allProducts = response.body()?.map { it.toDomain() }
                if (allProducts == null) {
                    onSuccess(emptyList())
                    return
                }
                onSuccess(Products(allProducts).getItemsInRange(start, size).items)
            }

            override fun onFailure(call: Call<List<ProductDataModel>>, t: Throwable) {
                Log.d("HttpError", t.message.toString())
                throw (t)
            }
        })
    }
}
