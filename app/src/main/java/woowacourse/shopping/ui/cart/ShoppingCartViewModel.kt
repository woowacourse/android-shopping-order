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
import woowacourse.shopping.data.remote.retrofit.toApiFailure
import woowacourse.shopping.data.remote.retrofit.toUserMessage
import woowacourse.shopping.domain.model.ShoppingCartItem
import woowacourse.shopping.domain.repository.ShoppingCartRepository

class ShoppingCartViewModel(
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
                    shoppingCartRepository.requestCartItems(
                        page = DEFAULT_PAGE,
                        size = DEFAULT_SIZE,
                        sort = null,
                    )
                    markCartLoaded()
                } catch (throwable: Throwable) {
                    _errorMessage.value =
                        throwable
                            .toApiFailure()
                            .toUserMessage(defaultMessage = "장바구니를 불러오지 못했습니다.")
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
                    shoppingCartRepository.addOrIncreaseByProductId(
                        productId = productId,
                        amount = amount,
                    )
                }.onSuccess {
                    markCartLoaded()
                    onSuccess?.invoke()
                }.onFailure { throwable ->
                    _errorMessage.value =
                        throwable
                            .toApiFailure()
                            .toUserMessage(defaultMessage = "장바구니 수량을 변경하지 못했습니다.")
                }
            }
        }
    }

    fun decreaseByProductId(productId: Long) {
        _errorMessage.value = null
        viewModelScope.launch {
            cartRequestMutex.withLock {
                runCatching {
                    shoppingCartRepository.decreaseByProductId(productId = productId)
                }.onSuccess {
                    markCartLoaded()
                }.onFailure { throwable ->
                    _errorMessage.value =
                        throwable
                            .toApiFailure()
                            .toUserMessage(defaultMessage = "장바구니 수량을 변경하지 못했습니다.")
                }
            }
        }
    }

    fun removeShoppingItem(shoppingCartItem: ShoppingCartItem) {
        _errorMessage.value = null
        viewModelScope.launch {
            cartRequestMutex.withLock {
                runCatching {
                    val productId = shoppingCartItem.product.id
                    shoppingCartRepository.removeByProductId(productId = productId)
                }.onSuccess {
                    markCartLoaded()
                }.onFailure { throwable ->
                    _errorMessage.value =
                        throwable
                            .toApiFailure()
                            .toUserMessage(defaultMessage = "장바구니 상품을 삭제하지 못했습니다.")
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
    }
}
