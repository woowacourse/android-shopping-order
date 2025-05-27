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
    private val defaultProductRepository: DefaultProductRepository,
) : ViewModel() {
    private val _uiState = MutableLiveData<ProductUiState>()
    val uiState: LiveData<ProductUiState> get() = _uiState

    private val _uiEvent = MutableSingleLiveData<MainUiEvent>()
    val uiEvent: SingleLiveData<MainUiEvent> get() = _uiEvent

    private val _isLoading = MutableSingleLiveData(true)
    val isLoading: SingleLiveData<Boolean> get() = _isLoading

    init {
        loadInitial()
    }

    private fun loadInitial() {
        defaultProductRepository.loadSinglePage(INITIAL_PAGE, PAGE_SIZE) { result ->
            result.fold(
                onSuccess = { page ->
                    val productList: List<Product> = page.products
                    val newState =
                        productList.map { product -> ProductState(product, Quantity(10)) }

                    _uiState.value =
                        ProductUiState(
                            productItems = newState,
                            load = LoadState.of(page.hasNextPage),
                        )
                    handleLoading(false)
                },
                onFailure = { throwable ->
                    Log.e("ProductLoad", "Error loading products", throwable)
                },
            )
        }
    }

    fun loadPage() {
        withState(_uiState.value) { state ->
            val productItems = state.productItems
            val pageIndex = productItems.size / PAGE_SIZE
            defaultProductRepository.loadSinglePage(pageIndex, PAGE_SIZE) { result ->
                result.fold(
                    onSuccess = { page ->
                        val productList: List<Product> = page.products
                        val newState =
                            productList.map { product -> ProductState(product, Quantity(10)) }

                        _uiState.value =
                            state.copy(
                                productItems = state.productItems + newState,
                                load = LoadState.of(page.hasNextPage),
                            )
                    },
                    onFailure = { throwable ->
                        Log.e("ProductLoad", "Error loading products", throwable)
                    },
                )
            }
        }
    }

    fun decreaseCartQuantity(productId: Long) {
        withState(_uiState.value) { state ->
            val result = state.decreaseCartQuantity(productId)
            handleDecreaseQuantity(state, result, productId)
        }
    }

    fun increaseCartQuantity(productId: Long) {
        withState(_uiState.value) { state ->
            val result = state.canIncreaseCartQuantity(productId)
            handleIncreaseQuantity(state, result, productId)
        }
    }

    fun syncCartQuantities() {
        withState(_uiState.value) { state ->
            val productItems = state.productItems
            val productIds = state.productIds

            cartRepository.getCarts(productIds) { carts ->
//                val updated =
//                    state.mapIndexed { i, productState ->
//                        val quantity = carts.getOrNull(i)?.quantity ?: Quantity(0)
//                        productState.copy(cartQuantity = quantity)
//                    }
//                productItems.postValue(updated)
            }
        }
    }

    fun syncHistory() {
        historyLoader { historyStates ->
            // historyItems.postValue(historyStates)
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

    private fun handleIncreaseQuantity(
        uiState: ProductUiState,
        state: IncreaseState,
        productId: Long,
    ) {
        when (state) {
            is IncreaseState.CanIncrease -> {
                val newState = state.value
                _uiState.value = uiState.modifyUiState(newState)
                cartRepository.upsert(productId, newState.cartQuantity)
            }

            is IncreaseState.CannotIncrease -> sendEvent(MainUiEvent.ShowCannotIncrease(state.quantity))
        }
    }

    private fun handleDecreaseQuantity(
        uiState: ProductUiState,
        decreaseResult: ProductState,
        productId: Long,
    ) {
        _uiState.value = uiState.modifyUiState(decreaseResult)

        if (!decreaseResult.cartQuantity.hasQuantity()) {
            cartRepository.delete(productId)
        } else {
            cartRepository.upsert(productId, decreaseResult.cartQuantity)
        }
    }

    private fun sendEvent(event: MainUiEvent) {
        _uiEvent.setValue(event)
    }

    companion object {
        private const val INITIAL_PAGE = 0
        private const val PAGE_SIZE = 20
    }
}
