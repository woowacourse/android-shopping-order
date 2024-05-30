package woowacourse.shopping.data.repository.real

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

        thread {
            try {
                val page = offset / PRODUCT_LOAD_PAGING_SIZE
                val response =
                    productDataSource.loadProducts(page, PRODUCT_LOAD_PAGING_SIZE).execute()
                if (response.isSuccessful && response.body() != null) {
                    products = response.body()?.productDto?.map { it.toProduct() }
                }
            } catch (e: Exception) {
                exception = e
            } finally {
                latch.countDown()
            }
        }

        latch.awaitOrThrow(exception)
        return products ?: throw NoSuchDataException()
    }

    override fun loadCategoryProducts(
        size: Int,
        category: String,
    ): List<Product> {
        val latch = CountDownLatch(1)
        var products: List<Product>? = null
        var exception: Exception? = null

        thread {
            try {
                val response =
                    productDataSource.loadCategoryProducts(
                        page = DEFAULT_PAGE,
                        size = RECOMMEND_PRODUCT_LOAD_PAGING_SIZE,
                        category = category,
                    ).execute()
                if (response.isSuccessful && response.body() != null) {
                    products = response.body()?.productDto?.map { it.toProduct() }
                }
            } catch (e: Exception) {
                exception = e
            } finally {
                latch.countDown()
            }
        }
        latch.awaitOrThrow(exception)

        return products ?: throw NoSuchDataException()
    }

    override fun getProduct(productId: Long): Product {
        val latch = CountDownLatch(1)
        var product: Product? = null
        var exception: Exception? = null

        thread {
            try {
                val response = productDataSource.loadProduct(productId.toInt()).execute()
                if (response.isSuccessful && response.body() != null) {
                    product = response.body()?.toProduct()
                }
            } catch (e: Exception) {
                exception = e
            } finally {
                latch.countDown()
            }
        }

        latch.awaitOrThrow(exception)
        return product ?: throw NoSuchDataException()
    }

    companion object {
        const val DEFAULT_PAGE = 0
        const val RECOMMEND_PRODUCT_LOAD_PAGING_SIZE = 10
        const val PRODUCT_LOAD_PAGING_SIZE = 20
    }
}
