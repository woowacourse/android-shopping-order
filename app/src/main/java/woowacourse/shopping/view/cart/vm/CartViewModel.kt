package woowacourse.shopping.view.cart.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.view.cart.CartUiEvent
import woowacourse.shopping.view.cart.state.CartUiState
import woowacourse.shopping.view.cart.state.toCartState
import woowacourse.shopping.view.cart.vm.Paging.Companion.INITIAL_PAGE_NO
import woowacourse.shopping.view.cart.vm.Paging.Companion.PAGE_SIZE
import woowacourse.shopping.view.core.common.withState
import woowacourse.shopping.view.core.event.MutableSingleLiveData
import woowacourse.shopping.view.core.event.SingleLiveData
import woowacourse.shopping.view.loader.HistoryLoader

class CartViewModel(
    private val cartRepository: CartRepository,
    private val historyLoader: HistoryLoader,
) : ViewModel() {
    private val paging = Paging(initialPage = INITIAL_PAGE_NO, pageSize = PAGE_SIZE)

    private val _uiState = MutableLiveData<CartUiState>()
    val uiState: LiveData<CartUiState> get() = _uiState

    private val _event = MutableSingleLiveData<CartUiEvent>()
    val event: SingleLiveData<CartUiEvent> get() = _event

    private val _isLoading = MutableSingleLiveData(true)
    val isLoading: SingleLiveData<Boolean> get() = _isLoading

    init {
        loadCarts()
    }

    fun decreaseCartQuantity(productId: Long) {
        withState(_uiState.value) { state ->
            val newState = state.decreaseCartQuantity(productId)

            cartRepository.updateQuantity(productId, newState.quantity) { result ->
                result.fold(
                    onSuccess = {
                        _uiState.value = state.modifyUiState(newState)
                    },
                    onFailure = {},
                )
            }
        }
    }

    fun increaseCartQuantity(productId: Long) {
        withState(_uiState.value) { state ->
            val newState = state.increaseCartQuantity(productId)

            cartRepository.updateQuantity(productId, newState.quantity) { result ->
                result.fold(
                    onSuccess = { _uiState.value = state.modifyUiState(newState) },
                    onFailure = {},
                )
            }
        }
    }

    fun loadCarts() {
        setLoading(true)
        val nextPage = paging.getPageNo() - 1
        cartRepository.loadSinglePage(
            nextPage,
            PAGE_SIZE,
        ) { result ->
            result.fold(
                onSuccess = { value ->
                    val pageState = paging.createPageState(!value.hasNextPage)
                    val newItems = value.carts.map { it.toCartState(_uiState.value?.allChecked ?: false) }

                    val currentItems = _uiState.value?.items ?: emptyList()
                    val combinedItems = currentItems + newItems

                    _uiState.value = CartUiState(items = combinedItems, pageState = pageState)
                },
                onFailure = {},
            )
            setLoading(false)
        }
    }

    private fun loadHistories() {
        historyLoader.invoke {
        }
    }

    fun addPage() {
        paging.moveToNextPage()
        loadCarts()
    }

    fun subPage() {
        paging.moveToPreviousPage()
        loadCarts()
    }

    fun deleteProduct(productId: Long) {
        cartRepository.deleteCart(productId) {
            refresh()
        }
    }

    fun updateCheckedState(
        cartId: Long,
        isChecked: Boolean,
    ) {
        withState(_uiState.value) { state ->
            _uiState.value = state.modifyCheckedState(cartId, isChecked)
        }
    }

    fun changeAllStateChecked(isChecked: Boolean) {
        withState(_uiState.value) { state ->
            _uiState.value = state.setAllItemsChecked(isChecked)
        }
    }

    private fun refresh() {
        withState(_uiState.value) { state ->
            cartRepository.loadSinglePage(paging.getPageNo() - 1, PAGE_SIZE) { result ->
                result.fold(
                    onSuccess = { value ->
                        if (paging.resetToLastPageIfEmpty(value.carts)) {
                            refresh()
                            return@loadSinglePage
                        }

                        val pageState = paging.createPageState(!value.hasNextPage)
                        val carts = value.carts.map { it.toCartState(state.allChecked) }

                        _uiState.postValue(CartUiState(items = carts, pageState = pageState))
                    },
                    onFailure = {},
                )
            }
        }
    }

    private fun sendEvent(event: CartUiEvent) {
        _event.setValue(event)
    }

    private fun setLoading(isLoading: Boolean) {
        _isLoading.postValue(isLoading)
    }
}
