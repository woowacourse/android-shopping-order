package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.Carts
import woowacourse.shopping.domain.model.Products

interface ProductRepository {
    fun findCartByProductId(id: Long): Result<Cart>

    fun getPagingProduct(
        page: Int,
        pageSize: Int,
    ): Result<Products>

    fun getAllCarts(): Result<Carts>
}
