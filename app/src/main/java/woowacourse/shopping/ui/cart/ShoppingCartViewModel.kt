package woowacourse.shopping.ui.cart

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import woowacourse.shopping.data.remote.retrofit.awaitBody
import woowacourse.shopping.data.remote.retrofit.awaitCompletion
import woowacourse.shopping.data.remote.retrofit.dto.CartRequest
import woowacourse.shopping.data.remote.retrofit.repository.ShoppingCartRetrofitRepository
import woowacourse.shopping.data.mapper.toCartQuantity
import woowacourse.shopping.data.mapper.toDomainShoppingCartItems
import woowacourse.shopping.domain.model.ShoppingCartItem
import woowacourse.shopping.domain.repository.ShoppingCartRepository

class ShoppingCartViewModel(
    private val shoppingCartRetrofitRepository: ShoppingCartRetrofitRepository,
    private val shoppingCartRepository: ShoppingCartRepository,
) : ViewModel() {
    private val cartRequestMutex = Mutex()
    private var hasLoadedCartOnce: Boolean = false
    private var lastCartLoadedElapsedMs: Long = 0L
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
                _screenState.value = createShoppingCartItemsState(latestShoppingCartItems)
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

    fun refreshPagedItems() {
        publishCurrentPageState()
    }

    fun requestCartItems(force: Boolean = false) {
        if (shouldSkipCartRequest(force = force)) return
        _errorMessage.value = null
        viewModelScope.launch {
            cartRequestMutex.withLock {
                if (shouldSkipCartRequest(force = force)) return@withLock
                _isLoading.value = true
                try {
                    val loadedItems = loadCartItems()
                    syncShoppingCartItems(loadedItems)
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
                    val currentItems = ensureCartSnapshotLoaded()
                    val targetItem = findByProductId(currentItems, productId)
                    if (targetItem == null) {
                        shoppingCartRetrofitRepository
                            .addCartItem(
                                product = CartRequest(productId = productId, quantity = amount),
                            ).awaitCompletion(errorPrefix = "장바구니 추가 실패")
                    } else {
                        val updatedQuantity = targetItem.getQuantity() + amount
                        shoppingCartRetrofitRepository
                            .updateQuantityCartItem(
                                id = targetItem.getId().toInt(),
                                product = updatedQuantity.toCartQuantity(),
                            ).awaitCompletion("장바구니 수량 수정 실패")
                    }
                    loadCartItems()
                }.onSuccess { latestItems ->
                    syncShoppingCartItems(latestItems)
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
                    val currentItems = ensureCartSnapshotLoaded()
                    val targetItem =
                        findByProductId(currentItems, productId) ?: return@runCatching currentItems
                    val updatedQuantity = targetItem.getQuantity() - 1
                    if (updatedQuantity <= 0) {
                        shoppingCartRetrofitRepository
                            .deleteCartItem(
                                id = targetItem.getId().toInt(),
                            ).awaitCompletion(errorPrefix = "장바구니 삭제 실패")
                    } else {
                        shoppingCartRetrofitRepository
                            .updateQuantityCartItem(
                                id = targetItem.getId().toInt(),
                                product = updatedQuantity.toCartQuantity(),
                            ).awaitCompletion(errorPrefix = "장바구니 수량 수정 실패")
                    }
                    loadCartItems()
                }.onSuccess { latestItems ->
                    syncShoppingCartItems(latestItems)
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
                    val currentItems = ensureCartSnapshotLoaded()
                    val targetItem =
                        findByProductId(
                            shoppingCartItems = currentItems,
                            productId = shoppingCartItem.product.id,
                        ) ?: return@runCatching currentItems
                    shoppingCartRetrofitRepository
                        .deleteCartItem(
                            id = targetItem.getId().toInt(),
                        ).awaitCompletion(errorPrefix = "장바구니 삭제 실패")
                    loadCartItems()
                }.onSuccess { latestItems ->
                    syncShoppingCartItems(latestItems)
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

    private suspend fun loadCartItems(): List<ShoppingCartItem> {
        return shoppingCartRetrofitRepository
            .requestCartItems(
                page = DEFAULT_PAGE,
                size = DEFAULT_SIZE,
                sort = null,
            ).awaitBody(errorPrefix = "장바구니 조회 실패")
            .toDomainShoppingCartItems()
    }

    private suspend fun ensureCartSnapshotLoaded(): List<ShoppingCartItem> {
        if (hasLoadedCartOnce) return _shoppingCartItems.value
        val loadedItems = loadCartItems()
        syncShoppingCartItems(loadedItems)
        markCartLoaded()
        return loadedItems
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

    private fun syncShoppingCartItems(shoppingCartItems: List<ShoppingCartItem>) {
        _shoppingCartItems.value = shoppingCartItems
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
    }
}
