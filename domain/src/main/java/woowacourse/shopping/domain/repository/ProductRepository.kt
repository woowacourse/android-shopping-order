package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductsWithCartItemDTO

interface ProductRepository {
//    fun getAll(callBack: (List<Product>) -> Unit)
//    fun getProduct(id: Int, callBack: (Product?) -> Unit)
    fun getProductsByRange(lastId: Int, pageItemCount: Int, callBack: (ProductsWithCartItemDTO) -> Unit)
    fun getProductsById(ids: List<Int>, callBack: (List<Product>) -> Unit)
}
