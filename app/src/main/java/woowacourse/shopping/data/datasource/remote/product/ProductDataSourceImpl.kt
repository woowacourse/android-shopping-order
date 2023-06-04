package woowacourse.shopping.data.datasource.remote.product

import woowacourse.shopping.data.remote.ServicePool
import woowacourse.shopping.data.remote.response.ProductResponseDTO
import java.util.concurrent.Executors

class ProductDataSourceImpl : ProductDataSource {

    override fun getSubListProducts(limit: Int, scrollCount: Int): Result<List<ProductResponseDTO>> {
        val executor = Executors.newSingleThreadExecutor()
        val result = executor.submit<Result<List<ProductResponseDTO>>> {
            val response = ServicePool.productService.getProducts(limit, scrollCount).execute()
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
