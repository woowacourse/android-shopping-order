package woowacourse.shopping.data.datasource.remote.product

import woowacourse.shopping.data.datasource.remote.retrofit.RetrofitClient
import woowacourse.shopping.data.remote.request.ProductDTO
import java.util.concurrent.Executors

class ProductRemoteDataSourceImpl : ProductRemoteDataSource {

    override fun getSubListProducts(limit: Int, scrollCount: Int): Result<List<ProductDTO>> {
        val executor = Executors.newSingleThreadExecutor()
        val result = executor.submit<Result<List<ProductDTO>>> {
            val response =
                RetrofitClient.getInstance().productDataService.getProducts(limit, scrollCount)
                    .execute()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Throwable(response.message()))
            }
        }.get()
        executor.shutdown()
        return result
    }
}
