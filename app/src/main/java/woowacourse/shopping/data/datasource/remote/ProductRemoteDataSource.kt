package woowacourse.shopping.data.datasource.remote

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.dto.response.ProductDto
import woowacourse.shopping.data.dto.response.ProductResponseDto
import woowacourse.shopping.data.dto.response.toProduct
import woowacourse.shopping.data.model.PagedResult
import woowacourse.shopping.data.service.ProductApiService
import woowacourse.shopping.domain.model.Product
import java.util.concurrent.CountDownLatch

class ProductRemoteDataSource(
    private val productService: ProductApiService,
) {
    fun getProductById(
        id: Int,
        onResult: (Result<Product?>) -> Unit,
    ) {
        productService.getProductById(id = id).enqueue(
            object : Callback<ProductDto> {
                override fun onResponse(
                    call: Call<ProductDto>,
                    response: Response<ProductDto>,
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { body ->
                            onResult(Result.success(body.toProduct()))
                        } ?: onResult(Result.success(null))
                    } else {
                        onResult(Result.failure(Exception("HTTP ${response.code()}: ${response.message()}")))
                    }
                }

                override fun onFailure(
                    call: Call<ProductDto>,
                    t: Throwable,
                ) {
                    onResult(Result.failure(t))
                }
            },
        )
    }

    fun getProductsByIds(
        ids: List<Int>,
        onResult: (Result<List<Product>?>) -> Unit,
    ) {
        val latch = CountDownLatch(ids.size)
        val products = mutableListOf<Product>()
        val exceptions = mutableListOf<Throwable>()

        ids.forEach { id ->
            getProductById(id) { result ->
                result
                    .onSuccess { product ->
                        product?.let { products.add(it) }
                    }.onFailure { throwable ->
                        exceptions.add(throwable)
                    }
                latch.countDown()
            }
        }
        latch.await()
        if (exceptions.isNotEmpty()) {
            onResult(Result.failure(exceptions.first()))
        } else {
            onResult(Result.success(products))
        }
    }

    fun getPagedProducts(
        page: Int?,
        size: Int?,
        onResult: (Result<PagedResult<Product>>) -> Unit,
    ) {
        productService.getPagedProducts(page = page, size = size).enqueue(
            object :
                Callback<ProductResponseDto> {
                override fun onResponse(
                    call: Call<ProductResponseDto>,
                    response: Response<ProductResponseDto>,
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { body ->
                            val products = body.content.map { it.toProduct() }
                            val hasNext = body.last.not()
                            onResult(Result.success(PagedResult(products, hasNext)))
                        } ?: onResult(Result.success(PagedResult(emptyList(), false)))
                    } else {
                        onResult(Result.failure(Exception("HTTP ${response.code()}: ${response.message()}")))
                    }
                }

                override fun onFailure(
                    call: Call<ProductResponseDto>,
                    t: Throwable,
                ) {
                    onResult(Result.failure(t))
                }
            },
        )
    }
}
