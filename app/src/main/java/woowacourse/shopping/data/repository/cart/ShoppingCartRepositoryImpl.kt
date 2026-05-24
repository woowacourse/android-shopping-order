package woowacourse.shopping.data.repository.cart

import android.os.SystemClock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import woowacourse.shopping.data.datasource.remote.cart.ShoppingCartRemoteDataSource
import woowacourse.shopping.data.mapper.toCartQuantity
import woowacourse.shopping.data.mapper.toDomainShoppingCartItems
import woowacourse.shopping.data.remote.retrofit.dto.CartRequest
import woowacourse.shopping.domain.model.ShoppingCartItem
import woowacourse.shopping.domain.model.ShoppingItem
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.domain.repository.ShoppingItemRepository

class ShoppingCartRepositoryImpl(
    private val shoppingItemRepository: ShoppingItemRepository,
    private val shoppingCartRemoteDataSource: ShoppingCartRemoteDataSource,
) : ShoppingCartRepository {
    private val cartItemsState = MutableStateFlow<List<ShoppingCartItem>>(emptyList())

    private val remoteStateMutex = Mutex()
    private var hasLoadedRemoteSnapshot: Boolean = false
    private var lastRemoteSnapshotLoadedElapsedMs: Long = 0L
    private var remoteCartItemIdByProductId: Map<Long, Int> = emptyMap()

    override fun observeShoppingItems(): Flow<List<ShoppingCartItem>> = cartItemsState.asStateFlow()

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

    override suspend fun decreaseByProductId(productId: Long) =
        remoteStateMutex.withLock {
            decreaseByProductIdInternal(productId = productId)
        }

    override suspend fun removeByProductId(productId: Long) =
        remoteStateMutex.withLock {
            removeByProductIdInternal(productId = productId)
        }

    private suspend fun requestCartItemsInternal(
        page: Int,
        size: Int,
        sort: List<String>?,
    ) {
        val remoteCartItems =
            shoppingCartRemoteDataSource
                .requestCartItems(
                    page = page,
                    size = size,
                    sort = sort,
                ).toDomainShoppingCartItems()

        remoteCartItemIdByProductId =
            remoteCartItems.associate { remoteCartItem ->
                remoteCartItem.product.id to remoteCartItem.getId().toInt()
            }

        syncShoppingItemsState(remoteCartItems)
        cartItemsState.value = remoteCartItems
        markRemoteSnapshotLoaded()
    }

    private suspend fun addOrIncreaseByProductIdInternal(
        productId: Long,
        amount: Int,
    ) {
        if (amount <= 0) return
        ensureRemoteSnapshotLoadedInternal()

        val currentQuantity = getCurrentCartQuantity(productId)
        val updatedQuantity = currentQuantity + amount
        val remoteCartItemId = remoteCartItemIdByProductId[productId]

        if (remoteCartItemId == null) {
            shoppingCartRemoteDataSource.addCartItem(
                product = CartRequest(productId = productId, quantity = amount),
            )
            requestCartItemsInternal(
                page = DEFAULT_PAGE,
                size = DEFAULT_SIZE,
                sort = null,
            )
            return
        }

        shoppingCartRemoteDataSource.updateQuantityCartItem(
            id = remoteCartItemId,
            product = updatedQuantity.toCartQuantity(),
        )

        applyLocalStateOrRefresh(
            productId = productId,
            targetQuantity = updatedQuantity,
            remoteCartItemId = remoteCartItemId.toLong(),
        )
        markRemoteSnapshotLoaded()
    }

    private suspend fun decreaseByProductIdInternal(productId: Long) {
        ensureRemoteSnapshotLoadedInternal()

        val remoteCartItemId = resolveRemoteCartItemIdInternal(productId = productId) ?: return
        val currentQuantity = getCurrentCartQuantity(productId)
        val updatedQuantity = currentQuantity - 1

        if (updatedQuantity <= 0) {
            shoppingCartRemoteDataSource.deleteCartItem(id = remoteCartItemId)
            remoteCartItemIdByProductId -= productId
        } else {
            shoppingCartRemoteDataSource.updateQuantityCartItem(
                id = remoteCartItemId,
                product = updatedQuantity.toCartQuantity(),
            )
        }

        applyLocalStateOrRefresh(
            productId = productId,
            targetQuantity = updatedQuantity.coerceAtLeast(0),
            remoteCartItemId = remoteCartItemId.toLong(),
        )
        markRemoteSnapshotLoaded()
    }

    private suspend fun removeByProductIdInternal(productId: Long) {
        ensureRemoteSnapshotLoadedInternal()

        val remoteCartItemId = resolveRemoteCartItemIdInternal(productId = productId) ?: return
        shoppingCartRemoteDataSource.deleteCartItem(id = remoteCartItemId)
        remoteCartItemIdByProductId -= productId

        applyLocalStateOrRefresh(
            productId = productId,
            targetQuantity = 0,
            remoteCartItemId = remoteCartItemId.toLong(),
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

    private fun getCurrentCartQuantity(productId: Long): Int =
        cartItemsState.value
            .firstOrNull { shoppingCartItem -> shoppingCartItem.product.id == productId }
            ?.getQuantity()
            ?: 0

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

    private suspend fun applyLocalStateOrRefresh(
        productId: Long,
        targetQuantity: Int,
        remoteCartItemId: Long,
    ) {
        val appliedQuantity = applyLocalQuantity(productId = productId, targetQuantity = targetQuantity)
        val appliedCartCache = applyCartCache(productId = productId, targetQuantity = targetQuantity, remoteCartItemId = remoteCartItemId)

        if (appliedQuantity && appliedCartCache) {
            return
        }

        requestCartItemsInternal(
            page = DEFAULT_PAGE,
            size = DEFAULT_SIZE,
            sort = null,
        )
    }

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
        return true
    }

    private fun applyCartCache(
        productId: Long,
        targetQuantity: Int,
        remoteCartItemId: Long,
    ): Boolean {
        val currentCartItems = cartItemsState.value
        val targetIndex =
            currentCartItems.indexOfFirst { shoppingCartItem ->
                shoppingCartItem.product.id == productId
            }

        if (targetQuantity <= 0) {
            if (targetIndex == -1) return true
            cartItemsState.value =
                currentCartItems.filterIndexed { index, _ ->
                    index != targetIndex
                }
            return true
        }

        val product =
            shoppingItemRepository
                .shoppingItems
                .value
                .firstOrNull { shoppingItem -> shoppingItem.getProductId() == productId }
                ?.getProduct()
                ?: return false

        val updatedCartItem =
            ShoppingCartItem(
                id = remoteCartItemId,
                shoppingItem =
                    ShoppingItem(
                        product = product,
                        quantity = targetQuantity,
                    ),
            )

        if (targetIndex == -1) {
            cartItemsState.value = currentCartItems + updatedCartItem
            return true
        }

        cartItemsState.value =
            currentCartItems.toMutableList().apply {
                this[targetIndex] = updatedCartItem
            }
        return true
    }

    private suspend fun syncShoppingItemsState(shoppingCartItems: List<ShoppingCartItem>) {
        shoppingItemRepository.upsertProducts(
            shoppingCartItems.map { shoppingCartItem -> shoppingCartItem.product },
        )

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
        }
    }

    private companion object {
        private const val DEFAULT_PAGE = 0
        private const val DEFAULT_SIZE = 20
        private const val REMOTE_CACHE_DURATION_MS = 30_000L
    }
}
