package woowacourse.shopping.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import woowacourse.shopping.model.ShoppingItem
import woowacourse.shopping.storage.room.shoppingItem.ShoppingItemDao
import woowacourse.shopping.storage.room.toDomain

class RoomShoppingItemRepository(
    private val shoppingItemDao: ShoppingItemDao,
    scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
) : ShoppingItemRepository {
    override val shoppingItems: StateFlow<List<ShoppingItem>> =
        shoppingItemDao
            .observeAll()
            .map { entities -> entities.map { entity -> entity.toDomain() } }
            .stateIn(scope, SharingStarted.Eagerly, emptyList())

    override fun getShoppingItemOrNull(productId: Long): ShoppingItem? =
        runBlocking {
            shoppingItemDao.getByProductId(productId)?.toDomain()
        }

    override fun getQuantity(productId: Long): Int =
        runBlocking {
            shoppingItemDao.getQuantityOrNull(productId)
                ?: throw IllegalArgumentException("해당 상품을 찾을 수 없습니다.")
        }

    override fun plusQuantity(productId: Long, amount: Int) {
        if (amount < 0) {
            throw IllegalArgumentException("상품의 수량 증가값은 음수일 수 없습니다.")
        }
        if (amount == 0) {
            return
        }
        runBlocking {
            val currentQuantity =
                shoppingItemDao.getQuantityOrNull(productId)
                    ?: throw IllegalArgumentException("해당 상품을 찾을 수 없습니다.")
            shoppingItemDao.updateQuantity(productId, currentQuantity + amount)
        }
    }

    override fun minusQuantity(productId: Long, amount: Int) {
        if (amount < 0) {
            throw IllegalArgumentException("상품의 수량 감소값은 음수일 수 없습니다.")
        }
        if (amount == 0) {
            return
        }
        runBlocking {
            val currentQuantity =
                shoppingItemDao.getQuantityOrNull(productId)
                    ?: throw IllegalArgumentException("해당 상품을 찾을 수 없습니다.")
            if (currentQuantity < amount) {
                throw IllegalArgumentException("상품의 수량은 0보다 작을 수 없습니다.")
            }
            shoppingItemDao.updateQuantity(productId, currentQuantity - amount)
        }
    }
}

