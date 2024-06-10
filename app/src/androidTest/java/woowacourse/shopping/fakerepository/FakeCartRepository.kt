package woowacourse.shopping.fakerepository

import com.example.data.datasource.local.entity.cart.CartItemEntity
import com.example.domain.model.Quantity
import com.example.domain.repository.CartRepository
import java.lang.IllegalArgumentException
import kotlin.math.min

class FakeCartRepository(savedCartItemEntities: List<CartItemEntity> = emptyList()) :
    CartRepository {
    private val cart: MutableList<CartItemEntity> = savedCartItemEntities.toMutableList()
    private var id: Int = 0

    override fun increaseQuantity(productId: Int) {
        val oldCartItem = cart.find { it.productId == productId }
        if (oldCartItem == null) {
            cart.add(CartItemEntity(id++, productId, Quantity(1)))
            return
        }
        cart.remove(oldCartItem)
        var quantity = oldCartItem.quantity
        cart.add(oldCartItem.copy(quantity = ++quantity))
    }

    override fun decreaseQuantity(productId: Int) {
        val oldCartItem = cart.find { it.productId == productId }
        oldCartItem ?: throw IllegalArgumentException()

        cart.remove(oldCartItem)
        if (oldCartItem.quantity.count == 1) {
            return
        }
        var quantity = oldCartItem.quantity
        cart.add(oldCartItem.copy(quantity = --quantity))
    }

    override fun changeQuantity(
        productId: Int,
        quantity: com.example.domain.model.Quantity,
    ) {
        val oldCartItem = cart.find { it.productId == productId }
        if (oldCartItem == null) {
            cart.add(CartItemEntity(id++, productId, quantity))
            return
        }
        cart.remove(oldCartItem)
        cart.add(oldCartItem.copy(quantity = quantity))
    }

    override fun deleteCartItem(cartItemId: Int) {
        val deleteCartItem =
            cart.find { it.productId == cartItemId } ?: throw IllegalArgumentException()
        cart.remove(deleteCartItem)
    }

    override fun find(productId: Int): CartItemEntity {
        return cart.find { it.productId == productId } ?: throw IllegalArgumentException()
    }

    override fun findRange(
        page: Int,
        pageSize: Int,
    ): List<CartItemEntity> {
        val fromIndex = page * pageSize
        val toIndex = min(fromIndex + pageSize, cart.size)
        return cart.subList(fromIndex, toIndex)
    }

    override fun totalCartItemCount(): Int = cart.size
}
