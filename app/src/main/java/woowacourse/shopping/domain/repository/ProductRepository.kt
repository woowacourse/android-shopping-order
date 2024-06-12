package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.Products

interface ProductRepository {
    suspend fun findCartByProductId(id: Long): Result<Cart>

    suspend fun getPagingProduct(
        page: Int,
        pageSize: Int,
    ): Result<Products>

    companion object {
        private var instance: ProductRepository? = null

        fun setInstance(productRepository: ProductRepository) {
            instance = productRepository
        }

        fun getInstance(): ProductRepository = requireNotNull(instance)
    }
}
