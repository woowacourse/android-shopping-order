package woowacourse.shopping.data.repository.real

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.remote.dto.product.ProductDto
import woowacourse.shopping.data.remote.dto.product.ProductResponse
import woowacourse.shopping.data.remote.source.ProductDataSourceImpl
import woowacourse.shopping.data.source.ProductDataSource
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.utils.DtoMapper.toProduct
import woowacourse.shopping.utils.exception.LatchUtils.awaitOrThrow
import woowacourse.shopping.utils.exception.NoSuchDataException
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread

class RealProductRepositoryImpl(
    private val productDataSource: ProductDataSource = ProductDataSourceImpl(),
) : ProductRepository {
    override fun loadPagingProducts(offset: Int): List<Product> {
        val latch = CountDownLatch(1)
        var products: List<Product>? = null
        var exception: Exception? = null

        productDataSource.loadProducts(offset, PRODUCT_LOAD_PAGING_SIZE)
            .enqueue(object : Callback<ProductResponse> {
                override fun onResponse(
                    call: Call<ProductResponse>,
                    response: Response<ProductResponse>
                ) {
                    products = response.body()?.productDto?.map { it.toProduct() }
                    latch.countDown()
                }

                override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                    exception = Exception(t.message)
                    latch.countDown()
                }

            })
        latch.awaitOrThrow(exception)

        return products ?: throw NoSuchDataException()
    }

    override fun getProduct(productId: Long): Product {
        val latch = CountDownLatch(1)
        var product: Product? = null
        var exception: Exception? = null
        thread {
            productDataSource.loadProduct(productId.toInt())
                .enqueue(object : Callback<ProductDto> {
                    override fun onResponse(p0: Call<ProductDto>, response: Response<ProductDto>) {
                        product = response.body()?.toProduct()
                        latch.countDown()
                    }

                    override fun onFailure(p0: Call<ProductDto>, t: Throwable) {
                        exception = Exception(t.message)
                        latch.countDown()
                    }

                })
        }
        latch.awaitOrThrow(exception)
        return product ?: throw NoSuchDataException()
    }

    companion object {
        const val PRODUCT_LOAD_PAGING_SIZE = 20
    }
}

