package woowacourse.shopping.data.repository

import woowacourse.shopping.data.mapper.toProduct
import woowacourse.shopping.data.remote.datasource.ProductDataSourceImpl
import woowacourse.shopping.domain.model.HomeInfo
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepositoryImpl(
    private val productDataSourceImpl: ProductDataSourceImpl,
) : ProductRepository {
    override suspend fun getProducts(
        category: String?,
        page: Int,
        size: Int,
        sort: String,
    ): Result<HomeInfo> {
        return productDataSourceImpl.getProductResponse(category, page, size, sort)
            .mapCatching { productResponse ->
                val products =
                    productResponse.products.map { productDto -> productDto.toProduct() }
                val canLoadMore = productResponse.last.not()
                HomeInfo(products, canLoadMore)
            }
    }

    override suspend fun getProductById(id: Int): Result<Product> {
        return productDataSourceImpl.getProductById(id).mapCatching {
            it.toProduct()
        }
    }
}
