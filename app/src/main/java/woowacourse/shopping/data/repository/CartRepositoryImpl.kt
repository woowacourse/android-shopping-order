package woowacourse.shopping.data.repository

import com.example.domain.model.CartProduct
import com.example.domain.repository.CartRepository
import woowacourse.shopping.data.datasource.remote.product.ProductDataSource

class CartRepositoryImpl(
    private val productDataSource: ProductDataSource,
) : CartRepository {
    override fun getAllProductInCart(): List<CartProduct> {
        TODO("Not yet implemented")
    }

    override fun insert(product: CartProduct) {
        TODO("Not yet implemented")
    }

    override fun remove(id: Long) {
        TODO("Not yet implemented")
    }

    override fun updateCount(id: Long, count: Int) {
        TODO("Not yet implemented")
    }

    override fun findById(id: Long): CartProduct? {
        TODO("Not yet implemented")
    }

    override fun getSubList(offset: Int, step: Int): List<CartProduct> {
        TODO("Not yet implemented")
    }
}
