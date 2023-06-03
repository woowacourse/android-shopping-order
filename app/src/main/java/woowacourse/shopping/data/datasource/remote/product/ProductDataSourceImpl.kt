package woowacourse.shopping.data.datasource.remote.product

import woowacourse.shopping.data.datasource.retrofit.ServicePool
import woowacourse.shopping.data.remote.request.ProductDTO
import java.util.concurrent.Executors

class ProductDataSourceImpl : ProductDataSource {

    override fun getSubListProducts(limit: Int, scrollCount: Int): Result<List<ProductDTO>> {
        val executor = Executors.newSingleThreadExecutor()
        val result = executor.submit<Result<List<ProductDTO>>> {
            val response = ServicePool.productDataService.getProducts(limit, scrollCount).execute()
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
