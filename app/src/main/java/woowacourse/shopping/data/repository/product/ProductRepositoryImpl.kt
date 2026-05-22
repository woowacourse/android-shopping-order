package woowacourse.shopping.data.repository.product

import woowacourse.shopping.data.datasource.remote.product.ProductRemoteDataSource
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepositoryImpl(
    private val productRemoteDataSource: ProductRemoteDataSource,
) : ProductRepository {
    override suspend fun requestProductPage(
        page: Int,
        size: Int,
        sort: List<String>?,
        category: String?,
    ): ProductRepository.ProductPageResult {
        val pageResult =
            productRemoteDataSource.requestProductPage(
                page = page,
                size = size,
                sort = sort,
                category = category,
            )
        return ProductRepository.ProductPageResult(
            products = pageResult.products,
            hasNextPage = pageResult.hasNextPage,
        )
    }

    override suspend fun requestProductDetail(id: Long): Product =
        productRemoteDataSource.requestProductDetail(id = id)
}
