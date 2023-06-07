package woowacourse.shopping.data.product

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.Product
import woowacourse.shopping.Products
import woowacourse.shopping.data.HttpErrorHandler
import woowacourse.shopping.data.common.model.BaseResponse
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.repository.ProductRepository

class ProductRepositoryImpl constructor(
    private val productRemoteDataSource: ProductRemoteDataSource,
    private val httpErrorHandler: HttpErrorHandler,
) : ProductRepository {
    override fun findProductById(id: Int, onSuccess: (Product?) -> Unit) {
        productRemoteDataSource.getProductById(id)
            .enqueue(object : Callback<BaseResponse<ProductDataModel>> {
                override fun onResponse(
                    call: Call<BaseResponse<ProductDataModel>>,
                    response: Response<BaseResponse<ProductDataModel>>,
                ) {
                    val result = response.body()?.result
                    onSuccess(result?.toDomain())
                }

                override fun onFailure(call: Call<BaseResponse<ProductDataModel>>, t: Throwable) {
                    httpErrorHandler.handleHttpError(t)
                }
            })
    }

    override fun getProductsWithRange(start: Int, size: Int, onSuccess: (List<Product>) -> Unit) {
        productRemoteDataSource.getAllProducts()
            .enqueue(object : Callback<BaseResponse<List<ProductDataModel>>> {
                override fun onResponse(
                    call: Call<BaseResponse<List<ProductDataModel>>>,
                    response: Response<BaseResponse<List<ProductDataModel>>>,
                ) {
                    val result = response.body()?.result
                    val allProducts = result?.map { it.toDomain() }
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
                    httpErrorHandler.handleHttpError(t)
                }
            })
    }
}
