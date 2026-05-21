package woowacourse.shopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.data.remote.server.apiresult.ApiResult
import woowacourse.shopping.data.remote.server.repository.CartRepository
import woowacourse.shopping.domain.PurchaseProducts
import woowacourse.shopping.ui.state.CartUiState

class CartViewModel(
    private val cartRepository: CartRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState = _uiState.asStateFlow()

    private val _allCartItems = MutableStateFlow<PurchaseProducts>(PurchaseProducts())

    private val _cartItemCount: MutableStateFlow<Int> = MutableStateFlow(0)

    init {
        fetchCart()
    }

    fun fetchCart() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val allItemsResult = cartRepository.getPagedCart(0, ViewModelConst.CART_MAX_COUNT)
            val pagedItemsResult = cartRepository.getPagedCart(_uiState.value.currentPage, PAGE_SIZE)

            if(allItemsResult is ApiResult.Success && pagedItemsResult is ApiResult.Success) {
                _allCartItems.update { allItemsResult.data }
                _cartItemCount.update { _allCartItems.value.totalCount() }
                val page = _uiState.value.currentPage
                val selectedPrice = calculateTotalPrice(_allCartItems.value, _uiState.value.checkedItemIds)

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
                _uiState.update { it.copy(errorMessage = errorMsg) }
            }
        }
    }

    fun next() {
        if(!_uiState.value.isNextEnable) return
        _uiState.update { it.copy(currentPage = it.currentPage + 1) }
        fetchCart()
    }

    fun prev() {
        if(!_uiState.value.isPrevEnable) return
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
            if(nextAmount < 1) return@launch

            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = cartRepository.updateCount(id, nextAmount)) {
                is ApiResult.Success -> fetchCart()
                is ApiResult.Error ->_uiState.update { it.copy(isLoading = false, errorMessage = "변경 실패 ${result.code}") }
                is ApiResult.Exception -> _uiState.update { it.copy(isLoading = false, errorMessage = "오류 발생: ${result.e.message}") }
            }
        }
    }

    fun removeWithID(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when(val result = cartRepository.deleteCartItem(id)) {
                is ApiResult.Success -> {
                    var newCheckedIds = emptyList<Long>()
                    if (_uiState.value.checkedItemIds.contains(id)) {
                        newCheckedIds = _uiState.value.checkedItemIds - id
                    }
                    var page = _uiState.value.currentPage
                    if (page > 0 && page * PAGE_SIZE >= _allCartItems.value.purchaseProducts.size - 1) page -= 1
                    _uiState.update { it.copy(checkedItemIds = newCheckedIds, currentPage = page) }
                    fetchCart()
                }
                is ApiResult.Error -> _uiState.update { it.copy(isLoading = false, errorMessage = "삭제 실패: ${result.message}") }
                is ApiResult.Exception ->_uiState.update { it.copy(isLoading = false, errorMessage = "오류 발생: ${result.e.message}") }
            }
        }
    }

    fun onItemChecked(id: Long) {
        _uiState.update { state ->
            val newCheckedIds = if (state.checkedItemIds.contains(id)) {
                state.checkedItemIds - id
            } else {
                state.checkedItemIds + id
            }
            state.copy(
                checkedItemIds = newCheckedIds,
                totalPrice = calculateTotalPrice(_allCartItems.value, newCheckedIds)
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
                totalPrice = calculateTotalPrice(_allCartItems.value, newCheckIds)
            )
        }
    }

    fun isAllChecked(): Boolean {
        val allIds = _allCartItems.value.purchaseProducts.map { it.id }
        return allIds.isNotEmpty() && _uiState.value.checkedItemIds.containsAll(allIds)
    }

    fun calculateTotalPrice(all: PurchaseProducts, checkedIds: List<Long>):Int {
        return all.purchaseProducts
            .filter { it.id in checkedIds }
            .sumOf { it.totalPrice() }
    }

    fun getErrorMessage(vararg results: ApiResult<*>):String {
        return results.filterIsInstance<ApiResult.Error>().firstOrNull()?.let { "서버 에러 ${it.code}" }
            ?: results.filterIsInstance<ApiResult.Exception>().firstOrNull()?.let { "예외 발생 ${it.e.message}" }
            ?: "알수 없는 에러가 발생했습니다."
    }

    companion object {
        private const val PAGE_SIZE = 5
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
