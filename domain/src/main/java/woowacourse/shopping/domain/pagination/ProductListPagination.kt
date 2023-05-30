package woowacourse.shopping.domain.pagination

import woowacourse.shopping.domain.model.ProductWithCartInfo
import woowacourse.shopping.domain.repository.ProductRepository

class ProductListPagination(
    private val rangeSize: Int,
    private val productRepository: ProductRepository
) : NextPagination<ProductWithCartInfo> {
    private var lastId = 0
    private var _isNextEnabled = true
    val isNextEnabled: Boolean
        get() = _isNextEnabled

    override fun fetchNextItems(callback: (List<ProductWithCartInfo>) -> Unit) {
        productRepository.getProductsByRange(lastId, rangeSize) {
            lastId = it.products.last().product.id
            _isNextEnabled = !it.last
            callback(it.products)
        }
    }
}
