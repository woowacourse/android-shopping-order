package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductInCart
import woowacourse.shopping.domain.util.WoowaResult

interface ProductRepository2 {

    fun getProduct(id: Long): WoowaResult<Product>
    fun getProducts(unit: Int, lastId: Long, callback: (List<ProductInCart>) -> Unit)
    fun isLastProduct(id: Long): Boolean
}
