package woowacourse.shopping.data.repository.cart

import woowacourse.shopping.data.datasource.cart.CartRemoteDataSource
import woowacourse.shopping.domain.cart.Quantity
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.repository.CartRepository

class CartRepositoryImpl(
    private val cartRemoteDataSource: CartRemoteDataSource,
) : CartRepository {
    override suspend fun addProduct(
        product: Product,
        quantity: Quantity,
    ) {
        cartRemoteDataSource.addCartItem(product.id, quantity.value)
    }

    override suspend fun increase(productId: Int) {
        cartRemoteDataSource.updateCartItem(productId, 1)
    }

    override suspend fun decrease(productId: Int) {
        cartRemoteDataSource.updateCartItem(productId, -1)
    }

    override suspend fun remove(productId: Int) {
        cartRemoteDataSource.deleteCartItem(productId)
    }
}
