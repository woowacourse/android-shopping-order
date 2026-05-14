package woowacourse.shopping.backend.retrofit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import woowacourse.shopping.backend.retrofit.awaitBody
import woowacourse.shopping.backend.retrofit.awaitCompletion
import woowacourse.shopping.backend.retrofit.dto.CartRequest
import woowacourse.shopping.backend.retrofit.repository.ShoppingCartRetrofitRepository
import woowacourse.shopping.mapper.toCartQuantity
import woowacourse.shopping.mapper.toDomainShoppingCartItems
import woowacourse.shopping.model.ShoppingCartItem

class ShoppingCartViewModel(
    private val shoppingCartRetrofitRepository: ShoppingCartRetrofitRepository,
) : ViewModel() {
    private val _shoppingCartItems = MutableStateFlow<List<ShoppingCartItem>>(emptyList())
    val shoppingCartItems: StateFlow<List<ShoppingCartItem>> = _shoppingCartItems.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _selectedCartItemIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedCartItemIds: StateFlow<Set<Long>> = _selectedCartItemIds.asStateFlow()


    fun requestCartItems() {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            runCatching {
                loadCartItems()
            }.onSuccess { loadedItems ->
                _shoppingCartItems.value = loadedItems
                _isLoading.value = false
                syncShoppingCartItems(loadedItems)
            }.onFailure { throwable ->
                _isLoading.value = false
                _errorMessage.value = throwable.message
            }
        }
    }

    fun addOrIncreaseByProductId(
        productId: Long,
        amount: Int = DEFAULT_QUANTITY,
        onSuccess: (() -> Unit)? = null,
    ) {
        if (amount <= 0) return
        viewModelScope.launch {
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
                _shoppingCartItems.value = latestItems
                onSuccess?.invoke()
                syncShoppingCartItems(latestItems)
            }
        }
    }

    fun decreaseByProductId(productId: Long) {
        viewModelScope.launch {
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
                _shoppingCartItems.value = latestItems
                syncShoppingCartItems(latestItems)
            }
        }
    }

    fun removeShoppingItem(shoppingCartItem: ShoppingCartItem) {
        viewModelScope.launch {
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
                _shoppingCartItems.value = latestItems
                syncShoppingCartItems(latestItems)
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

    fun getQuantityPrice(shoppingCartItem: ShoppingCartItem): Int =
        shoppingCartItem.getProductQuantityPrice()

    fun getTotalCount(): Int =
        _shoppingCartItems.value.sumOf { shoppingCartItem -> shoppingCartItem.getQuantity() }

    private suspend fun loadCartItems(): List<ShoppingCartItem> {
        val ShoppingCartItems = shoppingCartRetrofitRepository
            .requestCartItems(
                page = DEFAULT_PAGE,
                size = DEFAULT_SIZE,
                sort = null,
            ).awaitBody(errorPrefix = "장바구니 조회 실패")
            .toDomainShoppingCartItems()
        syncShoppingCartItems(ShoppingCartItems)
        return ShoppingCartItems
    }

    private fun findByProductId(
        shoppingCartItems: List<ShoppingCartItem>,
        productId: Long,
    ): ShoppingCartItem? =
        shoppingCartItems.firstOrNull { shoppingCartItem ->
            shoppingCartItem.product.id == productId
        }

    fun toggleShoppingCartItemSelection(shoppingCartItem: ShoppingCartItem) {
        val id = shoppingCartItem.getId()
        _selectedCartItemIds.value =
            _selectedCartItemIds.value.toMutableSet().apply {
                if (!add(id)) remove(id)
            }
    }

    fun setShoppingCartItemSelection(
        shoppingCartItemId: Long,
        isSelected: Boolean,
    ) {
        val validIds =
            _shoppingCartItems.value.map { shoppingCartItem -> shoppingCartItem.getId() }.toSet()
        if (shoppingCartItemId !in validIds) return
        _selectedCartItemIds.value =
            _selectedCartItemIds.value.toMutableSet().apply {
                if (isSelected) {
                    add(shoppingCartItemId)
                } else {
                    remove(shoppingCartItemId)
                }
            }
    }

    fun setShoppingCartItemsSelection(
        shoppingCartItemIds: List<Long>,
        isSelected: Boolean,
    ) {
        val validIds =
            _shoppingCartItems.value.map { shoppingCartItem -> shoppingCartItem.getId() }.toSet()
        val targetIds = shoppingCartItemIds.toSet().intersect(validIds)
        if (isSelected) {
            _selectedCartItemIds.value = targetIds
            return
        }
        _selectedCartItemIds.value = _selectedCartItemIds.value - targetIds
    }

    fun clearSelection() {
        _selectedCartItemIds.value = emptySet()
    }

    private fun syncShoppingCartItems(shoppingCartItems: List<ShoppingCartItem>) {
        _shoppingCartItems.value = shoppingCartItems
        val validIds = shoppingCartItems.map { it.getId() }.toSet()
        _selectedCartItemIds.value = _selectedCartItemIds.value.intersect(validIds)
    }

    fun getSelectedShoppingCartItems(): List<ShoppingCartItem> {
        val selectedIds = _selectedCartItemIds.value
        return _shoppingCartItems.value.filter { shoppingCartItem ->
            shoppingCartItem.getId() in selectedIds
        }
    }

    private companion object {
        private const val DEFAULT_PAGE = 0
        private const val DEFAULT_SIZE = 100
        private const val DEFAULT_QUANTITY = 1
    }
}
