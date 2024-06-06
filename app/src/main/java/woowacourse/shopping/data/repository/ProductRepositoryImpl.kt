package woowacourse.shopping.data.repository

import woowacourse.shopping.data.mapper.toProduct
import woowacourse.shopping.data.remote.datasource.ProductDataSourceImpl
import woowacourse.shopping.domain.model.HomeInfo
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
    ): Result<HomeInfo> {
        var result: Result<HomeInfo>? = null
        thread {
            productDataSourceImpl.getProductResponse(category, page, size, sort)
                .onSuccess { productResponse ->
                    val products =
                        productResponse.products.map { productDto -> productDto.toProduct() }
                    val canLoadMore = productResponse.last.not()
                    result = Result.success(HomeInfo(products, canLoadMore))
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
