package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.ProductWithCartInfo
import woowacourse.shopping.domain.model.ProductsWithCartItemDTO

interface ProductRepository {
    fun getProductsByRange(lastId: Int, pageItemCount: Int, callback: (ProductsWithCartItemDTO) -> Unit)
    fun getProductById(id: Int, callback: (ProductWithCartInfo) -> Unit)
}
