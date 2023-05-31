package woowacourse.shopping.data.cart

import woowacourse.shopping.domain.CartItem
import woowacourse.shopping.repository.CartItemRepository

class CartItemRepositoryImpl(
    private val cartItemDataSource: CartItemDataSource,
    private val cache: CartItemLocalCache = CartItemLocalCache
) : CartItemRepository {
    override fun save(cartItem: CartItem, onFinish: (CartItem) -> Unit) {
        cartItemDataSource.save(cartItem) { savedCartItem ->
            cache.save(savedCartItem)
            onFinish(savedCartItem)
        }
    }

    override fun findAll(onFinish: (List<CartItem>) -> Unit) {
        if (cache.isActivated) {
            onFinish(cache.findAll())
            return
        }

        cartItemDataSource.findAll { cartItems ->
            if (cache.isActivated.not()) cache.activate(cartItems)
            onFinish(cartItems)
        }
    }

    override fun findAllByIds(ids: List<Long>, onFinish: (List<CartItem>) -> Unit) {
        if (cache.isActivated) {
            onFinish(cache.findAll().filter { it.id in ids })
            return
        }

        cartItemDataSource.findAll { cartItems ->
            if (cache.isActivated.not()) cache.activate(cartItems)
            onFinish(cartItems.filter { it.id in ids })
        }
    }

    override fun findAllOrderByAddedTime(
        limit: Int,
        offset: Int,
        onFinish: (List<CartItem>) -> Unit
    ) {
        if (cache.isActivated) {
            val cartItems = cache.findAll()
            val slicedCartItems = cartItems.slice(offset until cartItems.size)
                .take(limit)
            onFinish(slicedCartItems)
            return
        }

        cartItemDataSource.findAll { cartItems ->
            if (cache.isActivated.not()) cache.activate(cartItems)
            val slicedCartItems = cartItems.slice(offset until cartItems.size)
                .take(limit)
            onFinish(slicedCartItems)
        }
    }

    override fun findById(id: Long, onFinish: (CartItem) -> Unit) {
        if (cache.isActivated) {
            val cartItem = cache.findAll().find { it.id == id }
            cartItem?.run(onFinish)
            return
        }

        cartItemDataSource.findAll { cartItems ->
            if (cache.isActivated.not()) cache.activate(cartItems)
            onFinish(cartItems.first { it.id == id })
        }
    }

    override fun findByProductId(productId: Long, onFinish: (CartItem?) -> Unit) {
        if (cache.isActivated) {
            val cartItem = cache.findAll().find { it.product.id == productId }
            cartItem?.run(onFinish)
            return
        }

        cartItemDataSource.findAll { cartItems ->
            if (cache.isActivated.not()) cache.activate(cartItems)
            onFinish(cartItems.find { it.product.id == productId })
        }
    }

    override fun countAll(onFinish: (Int) -> Unit) {
        if (cache.isActivated) {
            onFinish(cache.findAll().size)
            return
        }

        cartItemDataSource.findAll { cartItems ->
            if (cache.isActivated.not()) cache.activate(cartItems)
            onFinish(cartItems.size)
        }
    }

    override fun existByProductId(productId: Long, onFinish: (Boolean) -> Unit) {
        if (cache.isActivated) {
            onFinish(cache.findAll().any { it.product.id == productId })
            return
        }

        cartItemDataSource.findAll { cartItems ->
            if (cache.isActivated.not()) cache.activate(cartItems)
            onFinish(cartItems.any { it.product.id == productId })
        }
    }

    override fun updateCountById(id: Long, count: Int, onFinish: () -> Unit) {
        cartItemDataSource.updateCountById(id, count) {
            cache.updateCountById(id, count)
            onFinish()
        }
    }

    override fun deleteById(id: Long, onFinish: () -> Unit) {
        cartItemDataSource.deleteById(id) {
            cache.deleteById(id)
            onFinish()
        }
    }
}
