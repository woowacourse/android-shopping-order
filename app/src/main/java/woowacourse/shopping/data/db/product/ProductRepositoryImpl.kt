package woowacourse.shopping.data.db.shopping

import woowacourse.shopping.data.db.product.ProductMockWebServer
import woowacourse.shopping.data.db.product.ProductService
import woowacourse.shopping.data.model.Product2
import woowacourse.shopping.data.model.ProductResponse
import woowacourse.shopping.data.remote.RemoteProductDataSource
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.ProductRepository2
import kotlin.concurrent.thread

class ProductRepositoryImpl : ProductRepository {
    private val productMockWebServer: ProductService = ProductMockWebServer()

    private var offset = 0

    override fun findProductsByPage(): List<Product> {
        var pageProducts: List<Product> = emptyList()
        threadAction {
            val size = productMockWebServer.getSize()
            val start = offset
            offset = Integer.min(offset + 20, size)
            pageProducts = productMockWebServer.findPageProducts(start, offset)
        }
        return pageProducts
    }

    override fun findProductById(id: Int): Product? {
        var product: Product? = null
        threadAction {
            product = productMockWebServer.findProductById(id)
        }

        return product
    }

    override fun canLoadMore(): Boolean {
        var size = 0
        threadAction {
            size = productMockWebServer.getSize()
        }

        return !(size < 20 || offset == size)
    }

    private fun threadAction(action: () -> Unit) {
        val thread = Thread(action)
        thread.start()
        thread.join()
    }
}

class ProductRepositoryImpl2(
    private val remoteProductDataSource: RemoteProductDataSource,
) : ProductRepository2 {
    override fun getProducts(
        category: String?,
        page: Int,
        size: Int,
        sort: String,
    ): Result<ProductResponse> {
        var result: Result<ProductResponse>? = null
        thread {
            result =
                runCatching {
                    val response = remoteProductDataSource.getProducts(category, page, size, sort).execute()
                    if (response.isSuccessful) {
                        response.body() ?: throw Exception("No data available")
                    } else {
                        throw Exception("Error fetching data")
                    }
                }
        }.join()
        return result ?: throw Exception()
    }

    override fun getProductById(id: Int): Result<woowacourse.shopping.data.model.Product2> {
        var result: Result<Product2>? = null
        thread {
            result =
                runCatching {
                    val response = remoteProductDataSource.getProductById(id).execute()
                    if (response.isSuccessful) {
                        response.body() ?: throw Exception("No data available")
                    } else {
                        throw Exception("Error fetching data")
                    }
                }
        }.join()
        return result ?: throw Exception()
    }
}
