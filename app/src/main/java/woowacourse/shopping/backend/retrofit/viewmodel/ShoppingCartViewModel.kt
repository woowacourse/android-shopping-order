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

    private val _selectedProductIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedProductIds: StateFlow<Set<Long>> = _selectedProductIds.asStateFlow()


    fun requestCartItems() {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            runCatching {
                loadCartItems()
            }.onSuccess { loadedItems ->
                syncShoppingCartItems(loadedItems)
                _isLoading.value = false
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
                syncShoppingCartItems(latestItems)
            }
        }
    }

    fun removeShoppingItem(shoppingCartItem: ShoppingCartItem) {
        viewModelScope.launch {
            runCatching {
                shoppingCartRetrofitRepository
                    .deleteCartItem(
                        id = shoppingCartItem.getId().toInt(),
                    ).awaitCompletion(errorPrefix = "장바구니 삭제 실패")
                loadCartItems()
            }.onSuccess { latestItems ->
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
        val productId = shoppingCartItem.product.id
        _selectedProductIds.value =
            _selectedProductIds.value.toMutableSet().apply {
                if (!add(productId)) remove(productId)
            }
    }

    fun removeShoppingCartItemSelection(shoppingCartItem: ShoppingCartItem) {
        val productId = shoppingCartItem.product.id
        _selectedProductIds.value = _selectedProductIds.value - productId
    }

    fun clearSelection() {
        _selectedProductIds.value = emptySet()
    }

    private fun syncShoppingCartItems(shoppingCartItems: List<ShoppingCartItem>) {
        _shoppingCartItems.value = shoppingCartItems
        val validProductIds = shoppingCartItems.map { it.product.id }.toSet()
        _selectedProductIds.value = _selectedProductIds.value.intersect(validProductIds)
    }

    fun getSelectedShoppingCartItems(): List<ShoppingCartItem> {
        val selectedProductIds = _selectedProductIds.value
        return _shoppingCartItems.value.filter { shoppingCartItem ->
            shoppingCartItem.product.id in selectedProductIds
        }
    }
    private companion object {
        private const val DEFAULT_PAGE = 0
        private const val DEFAULT_SIZE = 100
        private const val DEFAULT_QUANTITY = 1
    }
}
