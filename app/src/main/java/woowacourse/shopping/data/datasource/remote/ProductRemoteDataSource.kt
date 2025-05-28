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
        onSuccess: (Product?) -> Unit,
    ) {
        productService.getProductById(id = id.toInt()).enqueue(
            object : Callback<ProductDto> {
                override fun onResponse(
                    call: Call<ProductDto>,
                    response: Response<ProductDto>,
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { body ->
                            onSuccess(body.toProduct())
                        } ?: onSuccess(null)
                    }
                }

                override fun onFailure(
                    call: Call<ProductDto>,
                    t: Throwable,
                ) {
                    println("error : $t")
                }
            },
        )
    }

    fun getProductsByIds(
        ids: List<Int>,
        onSuccess: (List<Product>?) -> Unit,
    ) {
        val latch = CountDownLatch(ids.size)
        val result = mutableListOf<Product>()
        ids.forEach { id ->
            getProductById(id) { product ->
                product?.let { result.add(it) }
                latch.countDown()
            }
        }
        latch.await()
        onSuccess(result)
    }

    fun getPagedProducts(
        page: Int?,
        size: Int?,
        onSuccess: (PagedResult<Product>) -> Unit,
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
                            onSuccess(PagedResult(products, hasNext))
                        } ?: onSuccess(PagedResult(emptyList(), false))
                    }
                }

                override fun onFailure(
                    call: Call<ProductResponseDto>,
                    t: Throwable,
                ) {
                    println("error : $t")
                }
            },
        )
    }
}
