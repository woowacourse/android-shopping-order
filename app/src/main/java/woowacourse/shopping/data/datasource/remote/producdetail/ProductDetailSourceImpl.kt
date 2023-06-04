package woowacourse.shopping.data.datasource.remote.producdetail

import woowacourse.shopping.data.remote.ServicePool
import woowacourse.shopping.data.remote.response.ProductResponseDTO
import java.util.concurrent.Executors

class ProductDetailSourceImpl : ProductDetailSource {
    override fun getById(id: Long): Result<ProductResponseDTO> {
        val executor = Executors.newSingleThreadExecutor()
        val result = executor.submit<Result<ProductResponseDTO>> {
            val response = ServicePool.productDetailService.getProductById(id).execute()
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
