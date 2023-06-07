package woowacourse.shopping.data.repository.impl

import woowacourse.shopping.data.datasource.ProductDataSource
import woowacourse.shopping.data.remote.dto.toDomain
import woowacourse.shopping.data.remote.result.DataResult
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.domain.model.ProductWithCartInfo
import woowacourse.shopping.domain.model.ProductsWithCartItem

class ProductRemoteRepository(
    private val productDataSource: ProductDataSource,
) : ProductRepository {
    override fun getProductsByRange(
        lastId: Int,
        pageItemCount: Int,
        callback: (DataResult<ProductsWithCartItem>) -> Unit,
    ) {
        productDataSource.getProductsByRange(lastId, pageItemCount) {
            when (it) {
                is DataResult.Success -> callback(DataResult.Success(it.response.toDomain()))
                is DataResult.Failure -> callback(it)
                is DataResult.NotSuccessfulError -> callback(it)
                is DataResult.WrongResponse -> callback(it)
            }
        }
    }

    override fun getProductById(id: Int, callback: (DataResult<ProductWithCartInfo>) -> Unit) {
        productDataSource.getProductById(id) {
            when (it) {
                is DataResult.Success -> callback(DataResult.Success(it.response.toDomain()))
                is DataResult.Failure -> callback(it)
                is DataResult.NotSuccessfulError -> callback(it)
                is DataResult.WrongResponse -> callback(it)
            }
        }
    }
}
