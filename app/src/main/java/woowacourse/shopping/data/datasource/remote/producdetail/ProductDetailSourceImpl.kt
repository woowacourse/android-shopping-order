package woowacourse.shopping.data.datasource.remote.producdetail

import woowacourse.shopping.data.datasource.remote.retrofit.ServicePool
import woowacourse.shopping.data.remote.request.ProductDTO
import java.util.concurrent.Executors

class ProductDetailSourceImpl : ProductDetailSource {
    override fun getById(id: Long): Result<ProductDTO> {
        val executor = Executors.newSingleThreadExecutor()
        val result = executor.submit<Result<ProductDTO>> {
            val response = ServicePool.productDataService.getProductById(id).execute()
            if (response.isSuccessful) {
                Result.success(response.body() ?: throw IllegalArgumentException())
            } else {
                Result.failure(Throwable(response.message()))
            }
        }.get()
        executor.shutdown()
        return result
    }
}
