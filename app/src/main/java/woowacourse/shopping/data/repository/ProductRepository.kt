package woowacourse.shopping.data.repository

import woowacourse.shopping.data.remote.result.DataResult
import woowacourse.shopping.domain.model.ProductWithCartInfo
import woowacourse.shopping.domain.model.ProductsWithCartItem

interface ProductRepository {
    fun getProductsByRange(lastId: Int, pageItemCount: Int, callback: (DataResult<ProductsWithCartItem>) -> Unit)
    fun getProductById(id: Int, callback: (DataResult<ProductWithCartInfo>) -> Unit)
}
