package woowacourse.shopping.data.datasource.remote.producdetail

import woowacourse.shopping.data.remote.ServiceFactory
import woowacourse.shopping.data.remote.response.ProductResponseDto
import java.util.concurrent.Executors

class ProductDetailSourceImpl : ProductDetailSource {
    override fun getById(id: Long): Result<ProductResponseDto> {
        val executor = Executors.newSingleThreadExecutor()
        val result = executor.submit<Result<ProductResponseDto>> {
            val response = ServiceFactory.productDetailService.getProductById(id).execute()
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
