package woowacourse.shopping.data.repository.cart

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
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.domain.repository.ShoppingItemRepository

class ShoppingCartRepositoryImpl(
    private val shoppingItemRepository: ShoppingItemRepository,
    private val shoppingCartRemoteDataSource: ShoppingCartRemoteDataSource,
) : ShoppingCartRepository {
    private val cartItemsState = MutableStateFlow<List<ShoppingCartItem>>(emptyList())

    private val remoteStateMutex = Mutex()
    private var remoteCartItemIdByProductId: Map<Long, Int> = emptyMap()
    private var remoteSnapshotState = RemoteSnapshotState()

    override fun observeShoppingItems(): Flow<List<ShoppingCartItem>> = cartItemsState.asStateFlow()

    override suspend fun requestCartItems(
        page: Int,
        size: Int,
        sort: List<String>?,
        force: Boolean,
    ) = remoteStateMutex.withLock {
        val normalizedPage = page.coerceAtLeast(DEFAULT_PAGE)
        val normalizedSize = size.coerceAtLeast(MIN_PAGE_SIZE)
        val normalizedSort = sort?.toList()

        val shouldResetSnapshot =
            force ||
                !hasRemoteSnapshotLoaded() ||
                normalizedSize != DEFAULT_SIZE ||
                normalizedSort != null

        if (shouldResetSnapshot) {
            val shouldLoadEntireSnapshot =
                (force || !hasRemoteSnapshotLoaded()) &&
                    normalizedSize == DEFAULT_SIZE &&
                    normalizedSort == null
            if (shouldLoadEntireSnapshot) {
                refreshEntireRemoteSnapshot()
                return@withLock
            }
            refreshRemoteSnapshot(
                targetPage = normalizedPage,
                size = normalizedSize,
                sort = normalizedSort,
            )
            return@withLock
        }
        val hasMoreRemotePagesToLoad = remoteSnapshotState.hasMorePages()
        val shouldLoadRequestedPage = normalizedPage >= remoteSnapshotState.loadedPageCount && hasMoreRemotePagesToLoad
        if (!shouldLoadRequestedPage) return@withLock
        refreshRemoteSnapshot(
            targetPage = normalizedPage,
            size = normalizedSize,
            sort = normalizedSort,
        )
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

    private suspend fun requestCartItemsInternal(remoteCartItems: List<ShoppingCartItem>) {
        remoteCartItemIdByProductId =
            remoteCartItems.associate { remoteCartItem ->
                remoteCartItem.product.id to remoteCartItem.getId().toInt()
            }

        syncShoppingItemsState(remoteCartItems)
        cartItemsState.value = remoteCartItems
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
            refreshEntireRemoteSnapshot()
            return
        }

        shoppingCartRemoteDataSource.updateQuantityCartItem(
            id = remoteCartItemId,
            product = updatedQuantity.toCartQuantity(),
        )
        refreshEntireRemoteSnapshot()
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
        refreshEntireRemoteSnapshot()
    }

    private suspend fun removeByProductIdInternal(productId: Long) {
        ensureRemoteSnapshotLoadedInternal()

        val remoteCartItemId = resolveRemoteCartItemIdInternal(productId = productId) ?: return
        shoppingCartRemoteDataSource.deleteCartItem(id = remoteCartItemId)
        remoteCartItemIdByProductId -= productId
        refreshEntireRemoteSnapshot()
    }

    private suspend fun ensureRemoteSnapshotLoadedInternal() {
        if (hasRemoteSnapshotLoaded()) return
        refreshEntireRemoteSnapshot()
    }

    private fun getCurrentCartQuantity(productId: Long): Int =
        cartItemsState.value
            .firstOrNull { shoppingCartItem -> shoppingCartItem.product.id == productId }
            ?.getQuantity()
            ?: 0

    private fun hasRemoteSnapshotLoaded(): Boolean = remoteSnapshotState.isLoaded

    private suspend fun resolveRemoteCartItemIdInternal(productId: Long): Int? {
        val existingRemoteCartItemId = remoteCartItemIdByProductId[productId]
        if (existingRemoteCartItemId != null) return existingRemoteCartItemId

        refreshEntireRemoteSnapshot()
        return remoteCartItemIdByProductId[productId]
    }

    private suspend fun refreshEntireRemoteSnapshot() {
        refreshRemoteSnapshot(
            targetPage = Int.MAX_VALUE,
            size = DEFAULT_SIZE,
            sort = null,
        )
    }

    private suspend fun refreshRemoteSnapshot(
        targetPage: Int,
        size: Int,
        sort: List<String>?,
    ) {
        val normalizedSize = size.coerceAtLeast(MIN_PAGE_SIZE)
        val normalizedTargetPage = targetPage.coerceAtLeast(DEFAULT_PAGE)
        val remoteSnapshot =
            loadRemotePagesUpTo(
                targetPage = normalizedTargetPage,
                size = normalizedSize,
                sort = sort,
            )

        remoteSnapshotState =
            remoteSnapshotState.copy(
                loadedPageCount = remoteSnapshot.loadedPageCount,
                totalPageCount = remoteSnapshot.totalPageCount,
                isLoaded = true,
            )
        requestCartItemsInternal(remoteCartItems = remoteSnapshot.items)
    }

    private suspend fun loadRemotePagesUpTo(
        targetPage: Int,
        size: Int,
        sort: List<String>?,
    ): RemoteCartSnapshot {
        val firstPageResponse =
            shoppingCartRemoteDataSource.requestCartItems(
                page = DEFAULT_PAGE,
                size = size,
                sort = sort,
            )

        val totalPages = firstPageResponse.totalPages.coerceAtLeast(0)
        if (totalPages <= 0) {
            return RemoteCartSnapshot(
                items = emptyList(),
                loadedPageCount = 0,
                totalPageCount = 0,
            )
        }

        val lastPageToLoad = targetPage.coerceAtMost(totalPages - 1)
        val remoteCartItems = firstPageResponse.toDomainShoppingCartItems().toMutableList()

        for (nextPage in 1..lastPageToLoad) {
            remoteCartItems +=
                shoppingCartRemoteDataSource
                    .requestCartItems(
                        page = nextPage,
                        size = size,
                        sort = sort,
                    ).toDomainShoppingCartItems()
        }

        return RemoteCartSnapshot(
            items = remoteCartItems,
            loadedPageCount = lastPageToLoad + 1,
            totalPageCount = totalPages,
        )
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
            syncLocalQuantityDifference(
                productId = productId,
                currentQuantity = currentQuantity,
                targetQuantity = targetQuantity,
            )
        }
    }

    private suspend fun syncLocalQuantityDifference(
        productId: Long,
        currentQuantity: Int,
        targetQuantity: Int,
    ) {
        when {
            targetQuantity > currentQuantity ->
                shoppingItemRepository.plusQuantity(productId, targetQuantity - currentQuantity)

            targetQuantity < currentQuantity ->
                shoppingItemRepository.minusQuantity(productId, currentQuantity - targetQuantity)
        }
    }

    private data class RemoteCartSnapshot(
        val items: List<ShoppingCartItem>,
        val loadedPageCount: Int,
        val totalPageCount: Int,
    )

    private data class RemoteSnapshotState(
        val loadedPageCount: Int = 0,
        val totalPageCount: Int = 0,
        val isLoaded: Boolean = false,
    ) {
        fun hasMorePages(): Boolean = loadedPageCount < totalPageCount
    }

    private companion object {
        private const val MIN_PAGE_SIZE = 1
        private const val DEFAULT_PAGE = 0
        private const val DEFAULT_SIZE = 20
    }
}
