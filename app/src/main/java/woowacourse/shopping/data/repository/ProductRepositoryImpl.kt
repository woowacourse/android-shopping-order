package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.remote.ProductRemoteDataSource
import woowacourse.shopping.data.dto.response.toProduct
import woowacourse.shopping.data.model.PagedResult
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepositoryImpl(
    private val remoteDataSource: ProductRemoteDataSource,
) : ProductRepository {
    override suspend fun getProductById(id: Int): Result<Product?> {
        return remoteDataSource.getProductById(id).mapCatching { productDto ->
            productDto?.toProduct()
        }
    }

    override suspend fun getProductsByIds(ids: List<Int>): Result<List<Product>?> {
        return remoteDataSource.getProductsByIds(ids).mapCatching { productDtos ->
            productDtos?.map { it.toProduct() }
        }
    }

    override suspend fun getPagedProducts(
        page: Int?,
        size: Int?,
    ): Result<PagedResult<Product>> {
        return remoteDataSource.getPagedProducts(page, size).mapCatching {
            PagedResult(
                it.items.map { productDto -> productDto.toProduct() },
                it.hasNext,
            )
        }
    }
}
