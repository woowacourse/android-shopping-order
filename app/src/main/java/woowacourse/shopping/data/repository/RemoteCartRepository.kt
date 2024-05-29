package woowacourse.shopping.data.repository

import woowacourse.shopping.data.database.ProductClient
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ShoppingCart
import woowacourse.shopping.domain.repository.CartRepository

class RemoteCartRepository : CartRepository {
    val service = ProductClient.service

    override fun insert(
        product: Product,
        quantity: Int,
    ) {
        TODO("Not yet implemented")
    }

    override fun update(
        productId: Long,
        quantity: Int,
    ) {
        TODO("Not yet implemented")
    }

    override fun updateQuantity(
        cartItemId: Long,
        quantity: Int,
    ) {
        TODO("Not yet implemented")
    }

    override fun updateQuantityWithProductId(
        productId: Long,
        quantity: Int,
    ) {
        TODO("Not yet implemented")
    }

    override fun findQuantityWithProductId(productId: Long): Int {
        TODO("Not yet implemented")
    }

    override fun size(): Int {
        TODO("Not yet implemented")
    }

    override fun sumOfQuantity(): Int {
        TODO("Not yet implemented")
    }

    override fun findOrNullWithProductId(productId: Long): CartItem? {
        TODO("Not yet implemented")
    }

    override fun findWithCartItemId(cartItemId: Long): CartItem {
        TODO("Not yet implemented")
    }

    override fun findAll(): ShoppingCart {
        TODO("Not yet implemented")
    }

    override fun findAllPagedItems(
        page: Int,
        pageSize: Int,
    ): ShoppingCart {
        TODO("Not yet implemented")
    }

    override fun delete(cartItemId: Long) {
        TODO("Not yet implemented")
    }

    override fun deleteWithProductId(productId: Long) {
        TODO("Not yet implemented")
    }

    override fun deleteAll() {
        TODO("Not yet implemented")
    }
}
