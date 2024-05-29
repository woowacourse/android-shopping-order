package woowacourse.shopping.data.cart.local

import woowacourse.shopping.data.cart.local.dao.CartDao
import woowacourse.shopping.data.cart.local.entity.CartItemEntity
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Quantity
import woowacourse.shopping.domain.repository.CartRepository
import kotlin.IllegalArgumentException
import kotlin.concurrent.thread

class RoomCartRepository(private val cartDao: CartDao) : CartRepository {
    override fun findAll(): List<CartItem> {
        var cartItems: List<CartItem> = emptyList()
        thread {
            cartItems = cartDao.findAll().toCartItems()
        }.join()
        return cartItems
    }

    override fun increaseQuantity(productId: Int) {
        thread {
            runCatching {
                cartDao.find(productId)
            }.onSuccess {
                var oldQuantity = it.quantity
                cartDao.changeQuantity(productId, ++oldQuantity)
            }.onFailure {
                val cartItemEntity = CartItemEntity(productId = productId, quantity = Quantity(1))
                cartDao.insert(cartItemEntity)
            }
        }.join()
    }

    override fun decreaseQuantity(productId: Int) {
        thread {
            runCatching {
                cartDao.find(productId)
            }.onSuccess {
                var oldQuantity = it.quantity
                if (oldQuantity.count == 1) {
                    cartDao.delete(productId)
                    return@thread
                }
                cartDao.changeQuantity(productId, --oldQuantity)
            }.onFailure {
                throw IllegalArgumentException(CANNOT_DELETE_MESSAGE)
            }
        }.join()
    }

    override fun changeQuantity(
        productId: Int,
        quantity: Quantity,
    ) {
        thread {
            runCatching {
                cartDao.find(productId)
            }.onSuccess {
                cartDao.changeQuantity(productId, quantity)
            }.onFailure {
                val cartItemEntity = CartItemEntity(productId = productId, quantity = quantity)
                cartDao.insert(cartItemEntity)
            }
        }.join()
    }

    override fun deleteCartItem(productId: Int) {
        thread {
            runCatching {
                cartDao.delete(productId)
            }.onFailure {
                throw IllegalArgumentException(CANNOT_DELETE_MESSAGE)
            }
        }.join()
    }

    override fun find(productId: Int): CartItem {
        var cartItemEntity: CartItemEntity? = null
        thread {
            cartItemEntity =
                runCatching { cartDao.find(productId) }
                    .getOrNull()
        }.join()
        return cartItemEntity?.toCartItem() ?: throw IllegalArgumentException(CANNOT_FIND_MESSAGE)
    }

    override fun findRange(
        page: Int,
        pageSize: Int,
    ): List<CartItem> {
        var cartItemEntities: List<CartItemEntity> = emptyList()
        thread {
            cartItemEntities = cartDao.findRange(page, pageSize)
        }.join()
        return cartItemEntities.toCartItems()
    }

    override fun totalCartItemCount(): Int {
        var totalCartItemCount = 0
        thread {
            totalCartItemCount = cartDao.totalCount()
        }.join()
        return totalCartItemCount
    }

    companion object {
        private const val CANNOT_DELETE_MESSAGE = "삭제할 수 없습니다."
        private const val CANNOT_FIND_MESSAGE = "해당하는 장바구니 상품이 존재하지 않습니다."
    }
}
