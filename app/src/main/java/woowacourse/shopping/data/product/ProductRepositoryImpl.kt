package woowacourse.shopping.data.product

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.Product
import woowacourse.shopping.Products
import woowacourse.shopping.data.common.BaseResponse
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.repository.ProductRepository

class ProductRepositoryImpl constructor(
    private val productDataSource: ProductDataSource,
) : ProductRepository {
    override fun findProductById(id: Int, onSuccess: (Product?) -> Unit) {
        productDataSource.getProductById(id)
            .enqueue(object : Callback<BaseResponse<ProductDataModel>> {
                override fun onResponse(
                    call: Call<BaseResponse<ProductDataModel>>,
                    response: Response<BaseResponse<ProductDataModel>>,
                ) {
                    onSuccess(response.body()?.result?.toDomain())
                }

                override fun onFailure(call: Call<BaseResponse<ProductDataModel>>, t: Throwable) {
                    Log.d("HttpError", t.message.toString())
                    throw (t)
                }
            })
    }

    override fun getProductsWithRange(start: Int, size: Int, onSuccess: (List<Product>) -> Unit) {
        productDataSource.getAllProducts()
            .enqueue(object : Callback<BaseResponse<List<ProductDataModel>>> {
                override fun onResponse(
                    call: Call<BaseResponse<List<ProductDataModel>>>,
                    response: Response<BaseResponse<List<ProductDataModel>>>,
                ) {
                    val allProducts = response.body()?.result?.map { it.toDomain() }
                    if (allProducts == null) {
                        onSuccess(emptyList())
                        return
                    }
                    onSuccess(Products(allProducts).getItemsInRange(start, size).items)
                }

                override fun onFailure(
                    call: Call<BaseResponse<List<ProductDataModel>>>,
                    t: Throwable
                ) {
                    Log.d("HttpError", t.message.toString())
                    throw (t)
                }
            })
    }
}
