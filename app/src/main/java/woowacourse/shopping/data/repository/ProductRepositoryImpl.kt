package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.remote.ProductRemoteDataSource
import woowacourse.shopping.data.model.PagedResult
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository
import kotlin.concurrent.thread

class ProductRepositoryImpl(
    private val remoteDataSource: ProductRemoteDataSource,
) : ProductRepository {
    override fun getProductById(
        id: Long,
        onSuccess: (Product?) -> Unit,
    ) {
        thread {
            remoteDataSource.getProductById(id, onSuccess)
        }
    }

    override fun getProductsByIds(
        ids: List<Long>,
        onSuccess: (List<Product>?) -> Unit,
    ) {
        val result = remoteDataSource.getProductsByIds(ids)
        onSuccess(result)
    }

    override fun getPagedProducts(
        page: Int,
        size: Int,
        onSuccess: (PagedResult<Product>) -> Unit,
    ) {
        require(page >= 0)
        require(size > 0)
        thread {
            remoteDataSource.getPagedProducts(page, size, onSuccess)
        }
    }
}
