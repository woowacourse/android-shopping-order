package woowacourse.shopping.data.repositoryImpl

import woowacourse.shopping.data.remoteDataSource.CartRemoteDataSource
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.model.CartProducts

class CartRepositoryImpl(
    private val remoteDataSource: CartRemoteDataSource
) : CartRepository {
    private val cartItems = CartProducts(emptyList())

    override fun getPage(index: Int, size: Int, callback: (CartProducts) -> Unit) {
        remoteDataSource.getAll {
            cartItems.replaceAll(it ?: emptyList())
            callback(cartItems)
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
        remoteDataSource.postItem(productId) {
            getAll {}
        }
    }

    override fun remove(id: Int, callback: () -> Unit) {
        remoteDataSource.deleteItem(id) {
            callback()
        }
    }

    override fun updateCount(id: Int, count: Int, callback: (Int?) -> Unit) {
        if (cartItems.isEmpty()) {
            remoteDataSource.getAll {
                cartItems.replaceAll(it ?: emptyList())
            }
        }
        val cartItem = cartItems.findByProductId(id)
        val updateCallback: (Int?) -> Unit = {
            getAll { }
            callback(it)
        }
        when {
            cartItem == null && count == 1 -> remoteDataSource.postItem(id, updateCallback)
            cartItem == null -> return
            count == 0 -> remoteDataSource.deleteItem(cartItem.id, updateCallback)
            count < 1 -> return
            else -> remoteDataSource.patchItemQuantity(cartItem.id, count, updateCallback)
        }
    }

    override fun updateChecked(id: Int, checked: Boolean) {
        cartItems.changeChecked(id, checked)
    }

    override fun getAll(callback: (CartProducts) -> Unit) {
        remoteDataSource.getAll {
            cartItems.replaceAll(it ?: emptyList())
            callback(CartProducts(it ?: emptyList()))
        }
    }
}
