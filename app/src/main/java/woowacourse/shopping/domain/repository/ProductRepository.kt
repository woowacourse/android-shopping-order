package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.Products

interface ProductRepository {
    fun getCartById(productId: Long): Result<Cart>

    fun getPagingProduct(
        page: Int,
        pageSize: Int,
    ): Result<Products>
}
