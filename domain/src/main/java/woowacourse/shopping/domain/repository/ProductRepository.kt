package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.util.WoowaResult

interface ProductRepository {

    fun fetchProduct(callback: (WoowaResult<CartProduct>) -> Unit, id: Long)
    fun fetchPagedProducts(
        callback: (products: WoowaResult<List<CartProduct>>, isLast: Boolean) -> Unit,
        pageItemCount: Int,
        lastId: Long,
    )
}
