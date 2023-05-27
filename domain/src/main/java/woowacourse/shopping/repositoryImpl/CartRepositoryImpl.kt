package woowacourse.shopping.repositoryImpl

import woowacourse.shopping.model.CartProducts
import woowacourse.shopping.repository.CartRepository
import woowacourse.shopping.service.RemoteCartService

class CartRepositoryImpl(
    private val remoteDatabase: RemoteCartService
) : CartRepository {
    private val cartItems = CartProducts(emptyList())

    override fun getPage(index: Int, size: Int, callback: (CartProducts) -> Unit) {
        if (cartItems.isEmpty()) {
            remoteDatabase.getAll {
                cartItems.replaceAll(it ?: emptyList())
                callback(cartItems)
            }
        }
    }

    override fun hasNextPage(index: Int, size: Int): Boolean {
        return index < cartItems.size / size
    }

    override fun hasPrevPage(index: Int, size: Int): Boolean {
        return index > 0
    }

    override fun getTotalCount(): Int {
        return cartItems.totalQuantity
    }

    override fun getTotalSelectedCount(): Int {
        return cartItems.totalCheckedQuantity
    }

    override fun getTotalPrice(): Int {
        return cartItems.totalPrice
    }

    override fun insert(productId: Int) {
        if (cartItems.findByProductId(productId) != null) {
            return
        }
        remoteDatabase.postItem(productId) {
            getAll {}
        }
    }

    override fun remove(id: Int) {
        remoteDatabase.deleteItem(id) {}
    }

    override fun updateCount(id: Int, count: Int, callback: (Int?) -> Unit) {
        if (cartItems.isEmpty()) {
            remoteDatabase.getAll {
                cartItems.replaceAll(it ?: emptyList())
            }
        }
        val cartItem = cartItems.findByProductId(id)
        when {
            cartItem == null && count == 1 -> remoteDatabase.postItem(id) {
                getAll { }
                callback(it)
            }
            cartItem == null -> return
            count == 0 -> remoteDatabase.deleteItem(cartItem.id) {
                getAll { }
                callback(it)
            }
            count < 1 -> return
            else -> remoteDatabase.patchItemQuantity(cartItem.id, count) {
                getAll { }
                callback(it)
            }
        }
    }

    override fun updateChecked(id: Int, checked: Boolean) {
        cartItems.changeChecked(id, checked)
    }

    override fun getAll(callback: (CartProducts) -> Unit) {
        remoteDatabase.getAll {
            cartItems.replaceAll(it ?: emptyList())
            callback(CartProducts(it ?: emptyList()))
        }
    }
}
