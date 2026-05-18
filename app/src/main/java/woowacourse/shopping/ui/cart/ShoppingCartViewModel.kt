package woowacourse.shopping.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

class ShoppingCartViewModel(
    private val shoppingCartRetrofitRepository: ShoppingCartRetrofitRepository,
) : ViewModel() {
    private val cartRequestMutex = Mutex()

    private val _shoppingCartItems = MutableStateFlow<List<ShoppingCartItem>>(emptyList())
    val shoppingCartItems: StateFlow<List<ShoppingCartItem>> = _shoppingCartItems.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _selectedProductIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedProductIds: StateFlow<Set<Long>> = _selectedProductIds.asStateFlow()

    fun requestCartItems() {
        _errorMessage.value = null
        viewModelScope.launch {
            cartRequestMutex.withLock {
                _isLoading.value = true
                try {
                    val loadedItems = loadCartItems()
                    syncShoppingCartItems(loadedItems)
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
                    val currentItems = loadCartItems()
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
                    val currentItems = loadCartItems()
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
                    val currentItems = loadCartItems()
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

    fun getTotalPrice(shoppingCartItems: List<ShoppingCartItem>): Int = shoppingCartItems.sumOf { it.getProductQuantityPrice() }

    private suspend fun loadCartItems(): List<ShoppingCartItem> {
        return shoppingCartRetrofitRepository
            .requestCartItems(
                page = DEFAULT_PAGE,
                size = DEFAULT_SIZE,
                sort = null,
            ).awaitBody(errorPrefix = "장바구니 조회 실패")
            .toDomainShoppingCartItems()
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
        _selectedProductIds.value = _selectedProductIds.value - targetProductIds
    }

    private fun syncShoppingCartItems(shoppingCartItems: List<ShoppingCartItem>) {
        _shoppingCartItems.value = shoppingCartItems
        val validProductIds = shoppingCartItems.map { it.product.id }.toSet()
        _selectedProductIds.value = _selectedProductIds.value.intersect(validProductIds)
    }

    private companion object {
        private const val DEFAULT_PAGE = 0
        private const val DEFAULT_SIZE = 20
        private const val DEFAULT_QUANTITY = 1
    }
}
