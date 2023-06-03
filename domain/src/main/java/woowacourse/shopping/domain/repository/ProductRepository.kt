package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductWithCartInfo
import woowacourse.shopping.domain.model.ProductsWithCartItemDTO

interface ProductRepository {
    fun getProductsByRange(lastId: Int, pageItemCount: Int, callBack: (ProductsWithCartItemDTO) -> Unit)
    fun getProductsById(ids: List<Int>, callback: (List<Product>) -> Unit)
    fun getProductById(id: Int, callback: (ProductWithCartInfo) -> Unit)
}
