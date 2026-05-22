package woowacourse.shopping.data.repository

import android.os.SystemClock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import woowacourse.shopping.data.mapper.toCartQuantity
import woowacourse.shopping.data.mapper.toDomainShoppingCartItems
import woowacourse.shopping.data.local.room.shoppingcart.ShoppingCartDao
import woowacourse.shopping.data.local.room.shoppingcart.ShoppingCartEntity
import woowacourse.shopping.data.local.room.shoppingcart.toDomain
import woowacourse.shopping.data.remote.retrofit.dto.CartRequest
import woowacourse.shopping.data.remote.retrofit.repository.ShoppingCartRetrofitRepository
import woowacourse.shopping.domain.model.ShoppingCartItem
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.domain.repository.ShoppingItemRepository

class RoomShoppingCartRepository(
    private val shoppingCartDao: ShoppingCartDao,
    private val shoppingItemRepository: ShoppingItemRepository,
    private val shoppingCartRetrofitRepository: ShoppingCartRetrofitRepository,
) : ShoppingCartRepository {
    /**
     * 이번에는 Repository, Data source에 대해 고민해보면 좋을 것 같은데요.
     *
     * ShoppingCartRepository 는 interface입니다.
     * 우리는 ShoppingCartRetrofitRepository와 RoomShoppingCartRepository를 가지고 있지만 RoomShoppingCartRepository만이 이를 구현하고 있어요.
     * 왜 이런 구현이 되었을까요?
     *
     * ShoppingCartRetrofitRepository와 RoomShoppingCartRepository 둘의 차이점을 가만히 생각해보면, 데이터가 내부 DB에서 오는가, 외부 API를 통해 가져오는가 입니다.
     *
     * 데이터의 출처만 다를뿐, 어떤 정보를 반환해야 하는가는 같을겁니다.
     * 이를 통해 한 번 개선해볼 수 있을까요?
     */
    private val remoteStateMutex = Mutex()
    private var hasLoadedRemoteSnapshot: Boolean = false
    private var lastRemoteSnapshotLoadedElapsedMs: Long = 0L
    private var remoteCartItemIdByProductId: Map<Long, Int> = emptyMap()

    override fun observeShoppingItems(): Flow<List<ShoppingCartItem>> =
        shoppingCartDao
            .observeShoppingCartItemRows()
            .map { shoppingCartItemRows -> shoppingCartItemRows.map { shoppingCartItemRow -> shoppingCartItemRow.toDomain() } }

    override suspend fun requestCartItems(
        page: Int,
        size: Int,
        sort: List<String>?,
        force: Boolean,
    ) = remoteStateMutex.withLock {
        if (!force && isRemoteSnapshotFresh()) return@withLock
        requestCartItemsInternal(page = page, size = size, sort = sort)
    }

    override suspend fun addOrIncreaseByProductId(
        productId: Long,
        amount: Int,
    ) = remoteStateMutex.withLock {
        addOrIncreaseByProductIdInternal(productId = productId, amount = amount)
    }

    override suspend fun decreaseByProductId(productId: Long) = remoteStateMutex.withLock {
        decreaseByProductIdInternal(productId = productId)
    }

    override suspend fun removeByProductId(productId: Long) = remoteStateMutex.withLock {
        removeByProductIdInternal(productId = productId)
    }

    private suspend fun requestCartItemsInternal(
        page: Int,
        size: Int,
        sort: List<String>?,
    ) {
        val remoteCartItems =
            shoppingCartRetrofitRepository
                .requestCartItems(
                    page = page,
                    size = size,
                    sort = sort,
                ).toDomainShoppingCartItems()
        remoteCartItemIdByProductId =
            remoteCartItems.associate { remoteCartItem ->
                remoteCartItem.product.id to remoteCartItem.getId().toInt()
            }
        syncLocalState(remoteCartItems)
        markRemoteSnapshotLoaded()
    }

    private suspend fun addOrIncreaseByProductIdInternal(
        productId: Long,
        amount: Int,
    ) {
        if (amount <= 0) return
        ensureRemoteSnapshotLoadedInternal()

        val currentQuantity = getLocalQuantity(productId)
        val updatedQuantity = currentQuantity + amount
        val remoteCartItemId = remoteCartItemIdByProductId[productId]
        if (remoteCartItemId == null) {
            shoppingCartRetrofitRepository.addCartItem(
                product = CartRequest(productId = productId, quantity = amount),
            )
            val resolvedRemoteCartItemId = resolveRemoteCartItemIdInternal(productId = productId)
            if (resolvedRemoteCartItemId != null) {
                remoteCartItemIdByProductId += productId to resolvedRemoteCartItemId
            }
        } else {
            shoppingCartRetrofitRepository.updateQuantityCartItem(
                id = remoteCartItemId,
                product = updatedQuantity.toCartQuantity(),
            )
        }
        applyLocalQuantityOrRefresh(
            productId = productId,
            targetQuantity = updatedQuantity,
        )
        markRemoteSnapshotLoaded()
    }

    private suspend fun decreaseByProductIdInternal(productId: Long) {
        ensureRemoteSnapshotLoadedInternal()

        val remoteCartItemId = resolveRemoteCartItemIdInternal(productId = productId) ?: return
        val currentQuantity = getLocalQuantity(productId)
        val updatedQuantity = currentQuantity - 1
        if (updatedQuantity <= 0) {
            shoppingCartRetrofitRepository.deleteCartItem(id = remoteCartItemId)
            remoteCartItemIdByProductId -= productId
        } else {
            shoppingCartRetrofitRepository.updateQuantityCartItem(
                id = remoteCartItemId,
                product = updatedQuantity.toCartQuantity(),
            )
        }
        applyLocalQuantityOrRefresh(
            productId = productId,
            targetQuantity = updatedQuantity.coerceAtLeast(0),
        )
        markRemoteSnapshotLoaded()
    }

    private suspend fun removeByProductIdInternal(productId: Long) {
        ensureRemoteSnapshotLoadedInternal()

        val remoteCartItemId = resolveRemoteCartItemIdInternal(productId = productId) ?: return
        shoppingCartRetrofitRepository.deleteCartItem(id = remoteCartItemId)
        remoteCartItemIdByProductId -= productId
        applyLocalQuantityOrRefresh(
            productId = productId,
            targetQuantity = 0,
        )
        markRemoteSnapshotLoaded()
    }

    private suspend fun ensureRemoteSnapshotLoadedInternal() {
        if (hasLoadedRemoteSnapshot) return
        requestCartItemsInternal(
            page = DEFAULT_PAGE,
            size = DEFAULT_SIZE,
            sort = null,
        )
    }

    private fun isRemoteSnapshotFresh(): Boolean {
        if (!hasLoadedRemoteSnapshot) return false
        return SystemClock.elapsedRealtime() - lastRemoteSnapshotLoadedElapsedMs < REMOTE_CACHE_DURATION_MS
    }

    private fun markRemoteSnapshotLoaded() {
        hasLoadedRemoteSnapshot = true
        lastRemoteSnapshotLoadedElapsedMs = SystemClock.elapsedRealtime()
    }

    private suspend fun resolveRemoteCartItemIdInternal(productId: Long): Int? {
        val existingRemoteCartItemId = remoteCartItemIdByProductId[productId]
        if (existingRemoteCartItemId != null) return existingRemoteCartItemId
        requestCartItemsInternal(
            page = DEFAULT_PAGE,
            size = DEFAULT_SIZE,
            sort = null,
        )
        return remoteCartItemIdByProductId[productId]
    }

    private suspend fun applyLocalQuantityOrRefresh(
        productId: Long,
        targetQuantity: Int,
    ) {
        if (applyLocalQuantity(productId = productId, targetQuantity = targetQuantity)) return
        requestCartItemsInternal(
            page = DEFAULT_PAGE,
            size = DEFAULT_SIZE,
            sort = null,
        )
    }

    private fun getLocalQuantity(productId: Long): Int =
        shoppingItemRepository
            .shoppingItems
            .value
            .firstOrNull { shoppingItem -> shoppingItem.getProductId() == productId }
            ?.getQuantity()
            ?: 0

    private suspend fun applyLocalQuantity(
        productId: Long,
        targetQuantity: Int,
    ): Boolean {
        val shoppingItem =
            shoppingItemRepository
                .shoppingItems
                .value
                .firstOrNull { currentShoppingItem ->
                    currentShoppingItem.getProductId() == productId
                }
                ?: return false

        val currentQuantity = shoppingItem.getQuantity()
        when {
            targetQuantity > currentQuantity ->
                shoppingItemRepository.plusQuantity(productId, targetQuantity - currentQuantity)

            targetQuantity < currentQuantity ->
                shoppingItemRepository.minusQuantity(productId, currentQuantity - targetQuantity)
        }
        if (targetQuantity > 0) {
            addLocalIfAbsent(productId)
        } else {
            removeLocalByProductId(productId)
        }
        return true
    }

    private suspend fun syncLocalState(shoppingCartItems: List<ShoppingCartItem>) {
        shoppingCartItems.forEach { shoppingCartItem ->
            shoppingItemRepository.upsertProduct(shoppingCartItem.product)
        }
        val quantityByProductId =
            shoppingCartItems.associate { shoppingCartItem ->
                shoppingCartItem.product.id to shoppingCartItem.getQuantity()
            }
        val localShoppingItems = shoppingItemRepository.shoppingItems.value
        localShoppingItems.forEach { shoppingItem ->
            val productId = shoppingItem.getProductId()
            val currentQuantity = shoppingItem.getQuantity()
            val targetQuantity = quantityByProductId[productId] ?: 0
            when {
                targetQuantity > currentQuantity ->
                    shoppingItemRepository.plusQuantity(productId, targetQuantity - currentQuantity)

                targetQuantity < currentQuantity ->
                    shoppingItemRepository.minusQuantity(productId, currentQuantity - targetQuantity)
            }
            if (targetQuantity > 0) {
                addLocalIfAbsent(productId)
            } else {
                removeLocalByProductId(productId)
            }
        }
    }

    private suspend fun addLocalIfAbsent(productId: Long) {
        shoppingCartDao.insert(
            ShoppingCartEntity(
                productId = productId,
            ),
        )
    }

    private suspend fun removeLocalByProductId(productId: Long) {
        shoppingCartDao.deleteByProductId(productId)
    }

    private companion object {
        private const val DEFAULT_PAGE = 0
        private const val DEFAULT_SIZE = 20
        private const val REMOTE_CACHE_DURATION_MS = 30_000L
    }
}
