package woowacourse.shopping.data.pagination

import woowacourse.shopping.data.remote.result.DataResult
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.domain.model.ProductWithCartInfo

class ProductListPagination(
    private val rangeSize: Int,
    private val productRepository: ProductRepository
) {
    private var lastId = 0
    private var _isNextEnabled = true
    val isNextEnabled: Boolean
        get() = _isNextEnabled

    fun fetchNextItems(callback: (DataResult<List<ProductWithCartInfo>>) -> Unit) {
        productRepository.getProductsByRange(lastId, rangeSize) {
            when (it) {
                is DataResult.Success -> {
                    lastId = it.response.products.lastOrNull()?.product?.id ?: 0
                    _isNextEnabled = !it.response.last
                    callback(DataResult.Success(it.response.products))
                }
                is DataResult.Failure -> {
                    callback(it)
                }
                is DataResult.WrongResponse -> {
                    callback(it)
                }
                is DataResult.NotSuccessfulError -> {
                    callback(it)
                }
            }
        }
    }
}
