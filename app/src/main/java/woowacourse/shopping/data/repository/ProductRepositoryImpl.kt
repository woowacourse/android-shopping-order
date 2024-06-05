package woowacourse.shopping.data.repository

import woowacourse.shopping.data.mapper.toProduct
import woowacourse.shopping.data.remote.datasource.ProductDataSourceImpl
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository
import kotlin.concurrent.thread

class ProductRepositoryImpl(
    private val productDataSourceImpl: ProductDataSourceImpl,
) : ProductRepository {
    override fun getProducts(
        category: String?,
        page: Int,
        size: Int,
        sort: String,
    ): Result<List<Product>> {
        var result: Result<List<Product>>? = null
        thread {
            productDataSourceImpl.getProducts(category, page, size, sort)
                .onSuccess {
                    val products = it.map { productDto -> productDto.toProduct() }
                    result = Result.success(products)
                }.onFailure {
                    result = Result.failure(IllegalArgumentException())
                }
        }.join()

        return result ?: throw Exception()
    }

    override fun getProductIsLast(
        category: String?,
        page: Int,
        size: Int,
        sort: String
    ): Result<Boolean> {
        var result: Result<Boolean>? = null
        thread {
            productDataSourceImpl.getProductIsLast(category, page, size, sort)
                .onSuccess {
                    result = Result.success(it)
                }.onFailure {
                    result = Result.failure(IllegalArgumentException())
                }
        }.join()

        return result ?: throw Exception()
    }


    override fun getProductById(id: Int): Result<Product> {
        var result: Result<Product>? = null
        thread {
            productDataSourceImpl.getProductById(id)
                .onSuccess {
                    val product = it.toProduct()
                    result = Result.success(product)
                }.onFailure {
                    result = Result.failure(IllegalArgumentException())
                }
        }.join()

        return result ?: throw Exception()
    }
}
