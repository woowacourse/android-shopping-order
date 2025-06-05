package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.remote.ProductRemoteDataSource
import woowacourse.shopping.data.model.PagedResult
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepositoryImpl(
    private val remoteDataSource: ProductRemoteDataSource,
) : ProductRepository {
    override fun getProductById(
        id: Int,
        onResult: (Result<Product?>) -> Unit,
    ) {
        remoteDataSource.getProductById(id, onResult)
    }

    override fun getProductsByIds(
        ids: List<Int>,
        onResult: (Result<List<Product>?>) -> Unit,
    ) {
        remoteDataSource.getProductsByIds(ids, onResult)
    }

    override fun getPagedProducts(
        page: Int?,
        size: Int?,
        onResult: (Result<PagedResult<Product>>) -> Unit,
    ) {
        remoteDataSource.getPagedProducts(page, size, onResult)
    }
}
