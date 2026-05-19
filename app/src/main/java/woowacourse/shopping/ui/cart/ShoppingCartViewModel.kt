package woowacourse.shopping.ui.cart

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import woowacourse.shopping.data.remote.retrofit.dto.CartRequest
import woowacourse.shopping.data.remote.retrofit.repository.ShoppingCartRetrofitRepository
import woowacourse.shopping.data.mapper.toCartQuantity
import woowacourse.shopping.data.mapper.toDomainShoppingCartItems
import woowacourse.shopping.data.remote.retrofit.sync.RemoteShoppingStateSyncer
import woowacourse.shopping.domain.model.ShoppingCartItem
import woowacourse.shopping.domain.model.ShoppingItem
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.domain.repository.ShoppingItemRepository

class ShoppingCartViewModel(
    private val shoppingCartRetrofitRepository: ShoppingCartRetrofitRepository,
    private val shoppingCartRepository: ShoppingCartRepository,
    private val remoteShoppingStateSyncer: RemoteShoppingStateSyncer,
    private val shoppingItemRepository: ShoppingItemRepository,
) : ViewModel() {

    private val cartRequestMutex = Mutex()
    private var hasLoadedCartOnce: Boolean = false
    private var lastCartLoadedElapsedMs: Long = 0L
    private var remoteCartItemIdByProductId: Map<Long, Int> = emptyMap()
    private val shoppingCartPageStateHolder = ShoppingCartPageStateHolder(shoppingCartItems = emptyList())
    private val _event = MutableSharedFlow<ShoppingCartEvent>(extraBufferCapacity = 1)
    val event: SharedFlow<ShoppingCartEvent> = _event.asSharedFlow()

    private val _shoppingCartItems = MutableStateFlow<List<ShoppingCartItem>>(emptyList())
    val shoppingCartItems: StateFlow<List<ShoppingCartItem>> = _shoppingCartItems.asStateFlow()
    private val _screenState: MutableStateFlow<ShoppingCartItemsState> =
        MutableStateFlow(createShoppingCartItemsState(emptyList()))
    val screenState: StateFlow<ShoppingCartItemsState> = _screenState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _selectedProductIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedProductIds: StateFlow<Set<Long>> = _selectedProductIds.asStateFlow()

    init {
        viewModelScope.launch {
            shoppingCartRepository.observeShoppingItems().collect { latestShoppingCartItems ->
                shoppingCartPageStateHolder.updateItems(latestShoppingCartItems)
                syncLocalShoppingCartItems(latestShoppingCartItems)
            }
        }
    }

    fun moveToPreviousPage() {
        shoppingCartPageStateHolder.beforePage()
        publishCurrentPageState()
    }

    fun moveToNextPage() {
        shoppingCartPageStateHolder.nextPage()
        publishCurrentPageState()
    }

    fun onBackClick() {
        _event.tryEmit(ShoppingCartEvent.NavigateBack)
    }

    fun getQuantityPrice(shoppingCartItem: ShoppingCartItem): Int = shoppingCartItem.getProductQuantityPrice()

    fun requestCartItems(force: Boolean = false) {
        if (shouldSkipCartRequest(force = force)) return
        _errorMessage.value = null
        viewModelScope.launch {
            cartRequestMutex.withLock {
                if (shouldSkipCartRequest(force = force)) return@withLock
                _isLoading.value = true
                try {
                    refreshCartItemsFromRemote()
                    markCartLoaded()
                } catch (throwable: Throwable) {
                    _errorMessage.value = throwable.message
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    fun addOrIncreaseByProductId(
        productId: Long,
        amount: Int = DEFAULT_QUANTITY,
        onSuccess: (() -> Unit)? = null,
    ) {
        if (amount <= 0) return
        _errorMessage.value = null
        viewModelScope.launch {
            cartRequestMutex.withLock {
                runCatching {
                    ensureRemoteSnapshotLoaded()
                    val currentItems = _shoppingCartItems.value
                    val currentItem = findByProductId(currentItems, productId)
                    if (currentItem == null) {
                        shoppingCartRetrofitRepository
                            .addCartItem(
                                product = CartRequest(productId = productId, quantity = amount),
                            )
                        remoteCartItemIdByProductId -= productId
                        val newCartItem = createProjectedCartItem(productId = productId, quantity = amount)
                        if (newCartItem == null) {
                            refreshCartItemsFromRemote()
                        } else {
                            syncProjectedCartItems(currentItems + newCartItem)
                        }
                    } else {
                        val updatedQuantity = currentItem.getQuantity() + amount
                        val remoteCartItemId = resolveRemoteCartItemId(productId = productId)
                        if (remoteCartItemId == null) {
                            shoppingCartRetrofitRepository
                                .addCartItem(
                                    product = CartRequest(productId = productId, quantity = updatedQuantity),
                                )
                            remoteCartItemIdByProductId -= productId
                        } else {
                            shoppingCartRetrofitRepository
                                .updateQuantityCartItem(
                                    id = remoteCartItemId,
                                    product = updatedQuantity.toCartQuantity(),
                                )
                        }
                        val projectedCartItems =
                            updateProjectedCartItemsQuantity(
                                currentItems = currentItems,
                                productId = productId,
                                quantity = updatedQuantity,
                            )
                        syncProjectedCartItems(projectedCartItems)
                    }
                }.onSuccess {
                    markCartLoaded()
                    onSuccess?.invoke()
                }.onFailure { throwable ->
                    _errorMessage.value = throwable.message
                }
            }
        }
    }

    fun decreaseByProductId(productId: Long) {
        _errorMessage.value = null
        viewModelScope.launch {
            cartRequestMutex.withLock {
                runCatching {
                    ensureRemoteSnapshotLoaded()
                    val currentItems = _shoppingCartItems.value
                    val targetItem = findByProductId(currentItems, productId) ?: return@runCatching
                    val remoteCartItemId = resolveRemoteCartItemId(productId = productId) ?: return@runCatching
                    val updatedQuantity = targetItem.getQuantity() - 1
                    if (updatedQuantity <= 0) {
                        shoppingCartRetrofitRepository
                            .deleteCartItem(
                                id = remoteCartItemId,
                            )
                        remoteCartItemIdByProductId -= productId
                        syncProjectedCartItems(
                            currentItems = currentItems.filterNot { cartItem -> cartItem.product.id == productId },
                        )
                    } else {
                        shoppingCartRetrofitRepository
                            .updateQuantityCartItem(
                                id = remoteCartItemId,
                                product = updatedQuantity.toCartQuantity(),
                            )
                        val projectedCartItems =
                            updateProjectedCartItemsQuantity(
                                currentItems = currentItems,
                                productId = productId,
                                quantity = updatedQuantity,
                            )
                        syncProjectedCartItems(projectedCartItems)
                    }
                }.onSuccess {
                    markCartLoaded()
                }.onFailure { throwable ->
                    _errorMessage.value = throwable.message
                }
            }
        }
    }

    fun removeShoppingItem(shoppingCartItem: ShoppingCartItem) {
        _errorMessage.value = null
        viewModelScope.launch {
            cartRequestMutex.withLock {
                runCatching {
                    ensureRemoteSnapshotLoaded()
                    val currentItems = _shoppingCartItems.value
                    val productId = shoppingCartItem.product.id
                    val targetItem = findByProductId(currentItems, productId) ?: return@runCatching
                    val remoteCartItemId = resolveRemoteCartItemId(productId = productId) ?: return@runCatching
                    shoppingCartRetrofitRepository
                        .deleteCartItem(
                            id = remoteCartItemId,
                        )
                    remoteCartItemIdByProductId -= targetItem.product.id
                    syncProjectedCartItems(
                        currentItems = currentItems.filterNot { cartItem -> cartItem.product.id == targetItem.product.id },
                    )
                }.onSuccess {
                    markCartLoaded()
                }.onFailure { throwable ->
                    _errorMessage.value = throwable.message
                }
            }
        }
    }

    fun increaseShoppingItemQuantity(shoppingCartItem: ShoppingCartItem) {
        val productId = shoppingCartItem.product.id
        addOrIncreaseByProductId(productId = productId, amount = 1)
    }

    fun decreaseShoppingItemQuantity(shoppingCartItem: ShoppingCartItem) {
        decreaseByProductId(productId = shoppingCartItem.product.id)
    }

    private suspend fun loadRemoteCartItems(): List<ShoppingCartItem> =
        shoppingCartRetrofitRepository
            .requestCartItems(
                page = DEFAULT_PAGE,
                size = DEFAULT_SIZE,
                sort = null,
            ).toDomainShoppingCartItems()

    private suspend fun refreshCartItemsFromRemote(): List<ShoppingCartItem> {
        val remoteCartItems = loadRemoteCartItems()
        remoteCartItemIdByProductId =
            remoteCartItems.associate { remoteCartItem ->
                remoteCartItem.product.id to remoteCartItem.getId().toInt()
            }
        withContext(Dispatchers.IO) {
            remoteShoppingStateSyncer.syncCartItems(remoteCartItems)
        }
        return remoteCartItems
    }

    private suspend fun ensureRemoteSnapshotLoaded() {
        if (hasLoadedCartOnce) return
        refreshCartItemsFromRemote()
        markCartLoaded()
    }

    private suspend fun resolveRemoteCartItemId(productId: Long): Int? {
        val existingRemoteCartItemId = remoteCartItemIdByProductId[productId]
        if (existingRemoteCartItemId != null) return existingRemoteCartItemId
        refreshCartItemsFromRemote()
        return remoteCartItemIdByProductId[productId]
    }

    private fun shouldSkipCartRequest(force: Boolean): Boolean {
        if (force) return false
        if (!hasLoadedCartOnce) return false
        return isCartCacheFresh()
    }

    private fun isCartCacheFresh(): Boolean =
        SystemClock.elapsedRealtime() - lastCartLoadedElapsedMs < CART_CACHE_DURATION_MS

    private fun markCartLoaded() {
        hasLoadedCartOnce = true
        lastCartLoadedElapsedMs = SystemClock.elapsedRealtime()
    }

    private fun findByProductId(
        shoppingCartItems: List<ShoppingCartItem>,
        productId: Long,
    ): ShoppingCartItem? =
        shoppingCartItems.firstOrNull { shoppingCartItem ->
            shoppingCartItem.product.id == productId
        }

    private fun createProjectedCartItem(
        productId: Long,
        quantity: Int,
    ): ShoppingCartItem? {
        val baseShoppingItem =
            shoppingItemRepository
                .shoppingItems
                .value
                .firstOrNull { shoppingItem -> shoppingItem.getProductId() == productId }
                ?: return null

        return ShoppingCartItem(
            id = TEMP_CART_ITEM_ID_BASE - productId,
            shoppingItem =
                ShoppingItem(
                    product = baseShoppingItem.getProduct(),
                    quantity = quantity,
                ),
        )
    }

    private fun updateProjectedCartItemsQuantity(
        currentItems: List<ShoppingCartItem>,
        productId: Long,
        quantity: Int,
    ): List<ShoppingCartItem> =
        currentItems.map { currentCartItem ->
            if (currentCartItem.product.id != productId) {
                currentCartItem
            } else {
                ShoppingCartItem(
                    id = currentCartItem.getId(),
                    shoppingItem =
                        ShoppingItem(
                            product = currentCartItem.product,
                            quantity = quantity,
                        ),
                )
            }
        }

    private suspend fun syncProjectedCartItems(currentItems: List<ShoppingCartItem>) {
        withContext(Dispatchers.IO) {
            remoteShoppingStateSyncer.syncCartItems(currentItems)
        }
    }

    fun setShoppingCartProductSelection(
        productId: Long,
        isSelected: Boolean,
    ) {
        val validProductIds =
            _shoppingCartItems.value.map { shoppingCartItem -> shoppingCartItem.product.id }.toSet()
        if (productId !in validProductIds) return
        _selectedProductIds.value =
            _selectedProductIds.value.toMutableSet().apply {
                if (isSelected) {
                    add(productId)
                } else {
                    remove(productId)
                }
            }
    }

    fun setShoppingCartProductsSelection(
        productIds: List<Long>,
        isSelected: Boolean,
    ) {
        val validProductIds =
            _shoppingCartItems.value.map { shoppingCartItem -> shoppingCartItem.product.id }.toSet()
        val targetProductIds = productIds.toSet().intersect(validProductIds)
        if (isSelected) {
            _selectedProductIds.value = targetProductIds
            return
        }
        _selectedProductIds.value -= targetProductIds
    }

    private fun syncLocalShoppingCartItems(shoppingCartItems: List<ShoppingCartItem>) {
        _shoppingCartItems.value = shoppingCartItems
        _screenState.value = createShoppingCartItemsState(shoppingCartItems)
        val validProductIds = shoppingCartItems.map { it.product.id }.toSet()
        _selectedProductIds.value = _selectedProductIds.value.intersect(validProductIds)
    }

    private fun publishCurrentPageState() {
        val currentItems = _screenState.value.items
        _screenState.value = createShoppingCartItemsState(currentItems)
    }

    private fun createShoppingCartItemsState(items: List<ShoppingCartItem>): ShoppingCartItemsState =
        ShoppingCartItemsState(
            items = items,
            pagedItems = shoppingCartPageStateHolder.getItems(),
            currentPage = shoppingCartPageStateHolder.currentPage,
            canMoveToPreviousPage = shoppingCartPageStateHolder.canMoveToPreviousPage(),
            canMoveToNextPage = shoppingCartPageStateHolder.canMoveToNextPage(),
        )

    data class ShoppingCartItemsState(
        val items: List<ShoppingCartItem>,
        val pagedItems: List<ShoppingCartItem> = emptyList(),
        val currentPage: Int = INITIAL_PAGE,
        val canMoveToPreviousPage: Boolean = false,
        val canMoveToNextPage: Boolean = false,
    )

    sealed interface ShoppingCartEvent {
        data object NavigateBack : ShoppingCartEvent
    }

    private companion object {
        private const val INITIAL_PAGE = 0
        private const val DEFAULT_PAGE = 0
        private const val DEFAULT_SIZE = 20
        private const val DEFAULT_QUANTITY = 1
        private const val CART_CACHE_DURATION_MS = 30_000L
        private const val TEMP_CART_ITEM_ID_BASE = -1_000_000L
    }
}
