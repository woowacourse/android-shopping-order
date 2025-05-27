package woowacourse.shopping.view.main.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.repository.DefaultProductRepository
import woowacourse.shopping.domain.Quantity
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.HistoryRepository
import woowacourse.shopping.view.core.common.withState
import woowacourse.shopping.view.core.event.MutableSingleLiveData
import woowacourse.shopping.view.core.event.SingleLiveData
import woowacourse.shopping.view.loader.HistoryLoader
import woowacourse.shopping.view.main.MainUiEvent
import woowacourse.shopping.view.main.state.IncreaseState
import woowacourse.shopping.view.main.state.LoadState
import woowacourse.shopping.view.main.state.ProductState
import woowacourse.shopping.view.main.state.ProductUiState

class MainViewModel(
    private val cartRepository: CartRepository,
    private val historyRepository: HistoryRepository,
    private val historyLoader: HistoryLoader,
    private val productRepository: DefaultProductRepository,
) : ViewModel() {

    private val _uiState = MutableLiveData<ProductUiState>()
    val uiState: LiveData<ProductUiState> get() = _uiState

    private val _uiEvent = MutableSingleLiveData<MainUiEvent>()
    val uiEvent: SingleLiveData<MainUiEvent> get() = _uiEvent

    private val _isLoading = MutableSingleLiveData(true)
    val isLoading: SingleLiveData<Boolean> get() = _isLoading

    init {
        loadPage(INITIAL_PAGE)
    }

    fun loadPage() {
        withState(_uiState.value) { state ->
            val pageIndex = state.productItems.size / PAGE_SIZE
            loadPage(pageIndex)
        }
    }

    private fun loadPage(pageIndex: Int) {
        productRepository.loadSinglePage(pageIndex, PAGE_SIZE) { result ->
            result.fold(
                onSuccess = { page ->
                    val newItems = page.products.map { ProductState(it, Quantity(10)) }
                    val currentItems = _uiState.value?.productItems.orEmpty()
                    _uiState.value = ProductUiState(
                        productItems = currentItems + newItems,
                        load = LoadState.of(page.hasNextPage)
                    )
                    handleLoading(false)
                },
                onFailure = { throwable ->
                    Log.e("ProductLoad", "Error loading products", throwable)
                    handleLoading(false)
                }
            )
        }
    }

    fun increaseCartQuantity(productId: Long) {
        withState(_uiState.value) { state ->
            when (val result = state.canIncreaseCartQuantity(productId)) {
                is IncreaseState.CanIncrease -> {
                    _uiState.value = state.modifyUiState(result.value)
                    cartRepository.upsert(productId, result.value.cartQuantity)
                }

                is IncreaseState.CannotIncrease -> {
                    sendEvent(MainUiEvent.ShowCannotIncrease(result.quantity))
                }
            }
        }
    }

    fun decreaseCartQuantity(productId: Long) {
        withState(_uiState.value) { state ->
            val updated = state.decreaseCartQuantity(productId)
            _uiState.value = state.modifyUiState(updated)

            if (!updated.cartQuantity.hasQuantity()) {
                cartRepository.delete(productId)
            } else {
                cartRepository.upsert(productId, updated.cartQuantity)
            }
        }
    }

    fun syncCartQuantities() {
        withState(_uiState.value) { state ->
            cartRepository.getCarts(state.productIds) { carts ->
                // 구현 예정
            }
        }
    }

    fun syncHistory() {
        historyLoader { historyStates ->
            // 구현 예정
        }
    }

    fun saveHistory(productId: Long) {
        withState(_uiState.value) { state ->
            historyRepository.saveHistory(productId) {
                _uiEvent.postValue(MainUiEvent.NavigateToDetail(productId, state.lastSeenProductId))
            }
        }
    }

    private fun handleLoading(isLoading: Boolean) {
        _isLoading.postValue(isLoading)
    }

    private fun sendEvent(event: MainUiEvent) {
        _uiEvent.setValue(event)
    }

    companion object {
        private const val INITIAL_PAGE = 0
        private const val PAGE_SIZE = 20
    }
}
