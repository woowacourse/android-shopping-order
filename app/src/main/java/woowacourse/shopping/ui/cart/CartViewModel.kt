package woowacourse.shopping.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.data.remote.server.apiresult.ApiResult
import woowacourse.shopping.data.remote.server.repository.CartRepository
import woowacourse.shopping.domain.PurchaseProducts
import woowacourse.shopping.ui.ViewModelConst

class CartViewModel(
    private val cartRepository: CartRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CartUiState())
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<CartEvent>()
    val event = _event.asSharedFlow()

    private val _allCartItems = MutableStateFlow<PurchaseProducts>(PurchaseProducts())

    private val _cartItemCount: MutableStateFlow<Int> = MutableStateFlow(0)

    init {
        fetchCart()
    }

    fun fetchCart() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val allItemsResult = cartRepository.getPagedCart(0, ViewModelConst.CART_MAX_COUNT)
            val pagedItemsResult =
                cartRepository.getPagedCart(_uiState.value.currentPage, PAGE_SIZE)

            if (allItemsResult is ApiResult.Success && pagedItemsResult is ApiResult.Success) {
                _allCartItems.update { allItemsResult.data }
                _cartItemCount.update { _allCartItems.value.totalCount() }
                val page = _uiState.value.currentPage
                val selectedPrice =
                    calculateTotalPrice(_allCartItems.value, _uiState.value.checkedItemIds)

                _uiState.update { state ->
                    state.copy(
                        items = pagedItemsResult.data,
                        isLoading = false,
                        isNextEnable = page < (_allCartItems.value.size() - 1) / PAGE_SIZE,
                        isPrevEnable = page > 0,
                        isPageable = _allCartItems.value.size() > PAGE_SIZE,
                        totalPrice = selectedPrice,
                    )
                }
            } else {
                val errorMsg = getErrorMessage(allItemsResult, pagedItemsResult)
                _event.emit(
                    CartEvent.SnackbarEvent(errorMsg)
                )
            }
        }
    }

    fun next() {
        if (!_uiState.value.isNextEnable) return
        _uiState.update { it.copy(currentPage = it.currentPage + 1) }
        fetchCart()
    }

    fun prev() {
        if (!_uiState.value.isPrevEnable) return
        _uiState.update { it.copy(currentPage = it.currentPage - 1) }
        fetchCart()
    }

    fun updateCountWithID(
        id: Long,
        updateAmount: Int,
    ) {
        viewModelScope.launch {
            val target = _allCartItems.value.findPurchaseProductById(id) ?: return@launch
            val nextAmount = target.count + updateAmount
            if (nextAmount < 1) return@launch

            _uiState.update { it.copy(isLoading = true) }

            when (val result = cartRepository.updateCount(id, nextAmount)) {
                is ApiResult.Success -> fetchCart()
                is ApiResult.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false)
                    }
                    _event.emit(
                        CartEvent.SnackbarEvent(
                            "${ViewModelConst.NETWORK_ERROR_LABEL}${result.code}"
                        )
                    )
                }

                is ApiResult.Exception -> {
                    _uiState.update {
                        it.copy(isLoading = false)
                    }
                    _event.emit(
                        CartEvent.SnackbarEvent(
                            "${ViewModelConst.ERROR_LABEL}${result.e.message}"
                        )
                    )
                }
            }
        }
    }

    fun removeWithID(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = cartRepository.deleteCartItem(id)) {
                is ApiResult.Success -> {
                    val newCheckedIds = _uiState.value.checkedItemIds - id
                    var page = _uiState.value.currentPage
                    if (page > 0 && page * PAGE_SIZE >= _allCartItems.value.size() - 1) page -= 1
                    _uiState.update { it.copy(checkedItemIds = newCheckedIds, currentPage = page) }
                    fetchCart()
                }

                is ApiResult.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false)
                    }
                    _event.emit(
                        CartEvent.SnackbarEvent(
                            "${ViewModelConst.NETWORK_ERROR_LABEL}${result.code}"
                        )
                    )
                }

                is ApiResult.Exception -> {
                    _uiState.update {
                        it.copy(isLoading = false)
                    }
                    _event.emit(
                        CartEvent.SnackbarEvent(
                            "${ViewModelConst.ERROR_LABEL}${result.e.message}"
                        )
                    )
                }
            }
        }
    }

    fun onItemChecked(id: Long) {
        _uiState.update { state ->
            val newCheckedIds =
                if (state.checkedItemIds.contains(id)) {
                    state.checkedItemIds - id
                } else {
                    state.checkedItemIds + id
                }
            state.copy(
                checkedItemIds = newCheckedIds,
                totalPrice = calculateTotalPrice(_allCartItems.value, newCheckedIds),
            )
        }
    }

    fun onSelectAllClick() {
        val allIds = _allCartItems.value.purchaseProducts.map { it.id }
        val isAllChecked = allIds.isNotEmpty() && _uiState.value.checkedItemIds.containsAll(allIds)
        val newCheckIds = if (isAllChecked) emptyList<Long>() else allIds
        _uiState.update {
            it.copy(
                checkedItemIds = newCheckIds,
                totalPrice = calculateTotalPrice(_allCartItems.value, newCheckIds),
            )
        }
    }

    fun isAllChecked(): Boolean {
        val allIds = _allCartItems.value.purchaseProducts.map { it.id }
        return allIds.isNotEmpty() && _uiState.value.checkedItemIds.containsAll(allIds)
    }

    fun calculateTotalPrice(
        all: PurchaseProducts,
        checkedIds: List<Long>,
    ): Int =
        all.purchaseProducts
            .filter { it.id in checkedIds }
            .sumOf { it.totalPrice() }

    fun getErrorMessage(vararg results: ApiResult<*>): String =
        results
            .filterIsInstance<ApiResult.Error>()
            .firstOrNull()
            ?.let { "${ViewModelConst.NETWORK_ERROR_LABEL}${it.code}" }
            ?: results
                .filterIsInstance<ApiResult.Exception>()
                .firstOrNull()
                ?.let { "${ViewModelConst.ERROR_LABEL}${it.e.message}" }
            ?: UNKNOWN_ERROR_LABEL

    fun navigateToShopping() {
        viewModelScope.launch {
            _event.emit(
                CartEvent.NavigateToShopping
            )
        }
    }

    fun navigateToRecommendation(totalPrice: Int, checkedIds: List<Long>) {
        if (checkedIds.isNotEmpty()) {
            viewModelScope.launch {
                _event.emit(
                    CartEvent.NavigateToRecommendation(
                        totalPrice = totalPrice,
                        checkedIds = checkedIds
                    )
                )
            }
        }
    }

    fun updateAmountTrigger(targetId: Long, updateAmount: Int) {
        viewModelScope.launch {
            _event.emit(
                CartEvent.UpdateCount(
                    targetId = targetId,
                    updateAmount = updateAmount
                )
            )
        }
    }

    fun removeItemTrigger(targetId: Long) {
        viewModelScope.launch {
            _event.emit(
                CartEvent.RemoveFromCart(targetId)
            )
        }
    }

    fun nextPageTrigger() {
        viewModelScope.launch {
            _event.emit(
                CartEvent.NextPage
            )
        }
    }

    fun prevPageTrigger() {
        viewModelScope.launch {
            _event.emit(
                CartEvent.PrevPage
            )
        }
    }

    companion object {
        private const val PAGE_SIZE = 5
        private const val UNKNOWN_ERROR_LABEL = "알수 없는 에러"
    }
}

class CartViewModelFactory(
    private val cartRepository: CartRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CartViewModel(cartRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
