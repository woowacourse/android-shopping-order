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
        onSuccess: (Product?) -> Unit,
    ) {
        remoteDataSource.getProductById(id, onSuccess)
    }

    override fun getProductsByIds(
        ids: List<Int>,
        onSuccess: (List<Product>?) -> Unit,
    ) {
        remoteDataSource.getProductsByIds(ids, onSuccess)
    }

    override fun getPagedProducts(
        page: Int,
        size: Int,
        onSuccess: (PagedResult<Product>) -> Unit,
    ) {
        require(page >= 0)
        require(size > 0)
        remoteDataSource.getPagedProducts(page, size, onSuccess)
    }
}
