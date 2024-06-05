package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.Products

interface ProductRepository {
    suspend fun getCartById(productId: Long): Result<Cart>

    suspend fun getPagingProduct(
        page: Int,
        pageSize: Int,
    ): Result<Products>
}
