package woowacourse.shopping.data.repository

import woowacourse.shopping.data.model.Product
import woowacourse.shopping.data.model.ProductResponse
import woowacourse.shopping.data.remote.RemoteProductDataSource
import woowacourse.shopping.domain.repository.ProductRepository
import kotlin.concurrent.thread

class ProductRepositoryImpl(
    private val remoteProductDataSource: RemoteProductDataSource,
) : ProductRepository {
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

    override fun getProductById(id: Int): Result<Product> {
        var result: Result<Product>? = null
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
