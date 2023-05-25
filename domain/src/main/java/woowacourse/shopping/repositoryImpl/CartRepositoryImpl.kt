package woowacourse.shopping.repositoryImpl

import java.lang.Integer.min
import woowacourse.shopping.model.CartProduct
import woowacourse.shopping.model.CartProducts
import woowacourse.shopping.repository.CartRepository
import woowacourse.shopping.service.RemoteCartService

class CartRepositoryImpl(
    private val remoteDatabase: RemoteCartService
) : CartRepository {
    private val cartItems = mutableListOf<CartProduct>()

    override fun getPage(index: Int, size: Int): CartProducts {
        if (cartItems.isEmpty()) {
            cartItems.addAll(remoteDatabase.getAll())
        }
        return CartProducts(
            cartItems.subList(index * size, min((index + 1) * size, cartItems.size))
        )
    }

    override fun hasNextPage(index: Int, size: Int): Boolean {
        return index < cartItems.size / size
    }

    override fun hasPrevPage(index: Int, size: Int): Boolean {
        return index > 0
    }

    override fun getTotalCount(): Int {
        return cartItems.sumOf { it.count }
    }

    override fun getTotalSelectedCount(): Int {
        return cartItems.filter { it.checked }.sumOf { it.count }
    }

    override fun getTotalPrice(): Int {
        return cartItems.filter { it.checked }.sumOf { it.price * it.count }
    }

    override fun insert(productId: Int) {
        if (cartItems.firstOrNull { it.productId == productId } != null) {
            return
        }
        remoteDatabase.postItem(productId)
        getAll()
    }

    override fun remove(id: Int) {
        remoteDatabase.deleteItem(id)
    }

    override fun updateCount(id: Int, count: Int): Int {
        val cartItem = cartItems.firstOrNull { it.productId == id }
        when {
            cartItem == null -> remoteDatabase.postItem(id)
            count < 1 -> remoteDatabase.deleteItem(cartItem.id)
            else -> remoteDatabase.patchItemQuantity(cartItem.id, count)
        }

        getAll()
        return cartItems.size
    }

    override fun updateChecked(id: Int, checked: Boolean) {
        cartItems.indexOfFirst { it.id == id }.let {
            cartItems[it] = cartItems[it].copy(checked = checked)
        }
    }

    override fun getAll(): CartProducts {
        remoteDatabase.getAll().let {
            cartItems.clear()
            cartItems.addAll(it)
            return CartProducts(it)
        }
    }
}
