package woowacourse.shopping.data.datasource.remote.product

import com.example.domain.util.CustomResult
import com.example.domain.util.Error.Disconnect
import woowacourse.shopping.data.remote.ServiceFactory
import woowacourse.shopping.data.remote.api.ShoppingService
import woowacourse.shopping.data.remote.response.ProductResponseDto
import woowacourse.shopping.utils.enqueueUtil
import java.util.concurrent.Executors

class NewShoppingDataSourceImpl(
    private val shoppingService: ShoppingService,
) : ProductDataSource {

    override fun getProducts(
        limit: Int,
        scrollCount: Int,
        onSuccess: (CustomResult<List<ProductResponseDto>>) -> Unit,
        onFailure: (CustomResult<Error>) -> Unit,
    ) {
        val response = shoppingService.getProducts(limit, scrollCount)
        response.enqueueUtil(
            onSuccess = { products ->
                onSuccess.invoke(CustomResult.SUCCESS(products))
            },
            onFailure = {
                onFailure.invoke(CustomResult.FAIL(Disconnect))
            },
        )
    }

    override fun getSubListProducts(
        limit: Int,
        scrollCount: Int,
    ): Result<List<ProductResponseDto>> {
        val executor = Executors.newSingleThreadExecutor()
        val result = executor.submit<Result<List<ProductResponseDto>>> {
            val response = ServiceFactory.shoppingService.getProducts(limit, scrollCount).execute()
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
