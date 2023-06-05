package woowacourse.shopping.data.repository

import woowacourse.shopping.data.remote.dto.ProductsWithCartItemDTO
import woowacourse.shopping.data.remote.result.DataResult
import woowacourse.shopping.domain.model.ProductWithCartInfo

interface ProductRepository {
    fun getProductsByRange(lastId: Int, pageItemCount: Int, callback: (DataResult<ProductsWithCartItemDTO>) -> Unit)
    fun getProductById(id: Int, callback: (DataResult<ProductWithCartInfo>) -> Unit)
}
