package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartProduct

interface ProductRepository {

    fun fetchProduct(callback: (Result<CartProduct>) -> Unit, id: Long)
    fun fetchPagedProducts(
        callback: (products: Result<List<CartProduct>>, isLast: Boolean) -> Unit,
        pageItemCount: Int,
        lastId: Long,
    )
}
