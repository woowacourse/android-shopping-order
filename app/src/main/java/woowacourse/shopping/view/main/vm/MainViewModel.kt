package woowacourse.shopping.view.main.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
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
import woowacourse.shopping.view.core.ext.addSourceList
import woowacourse.shopping.view.loader.HistoryLoader
import woowacourse.shopping.view.loader.ProductWithCartLoader
import woowacourse.shopping.view.main.MainUiEvent
import woowacourse.shopping.view.main.state.HistoryState
import woowacourse.shopping.view.main.state.IncreaseState
import woowacourse.shopping.view.main.state.LoadState
import woowacourse.shopping.view.main.state.ProductState
import woowacourse.shopping.view.main.state.ProductUiState

class MainViewModel(
    private val cartRepository: CartRepository,
    private val historyRepository: HistoryRepository,
    private val productWithCartLoader: ProductWithCartLoader,
    private val historyLoader: HistoryLoader,
    private val defaultProductRepository: DefaultProductRepository,
) : ViewModel() {
    private val productItems = MutableLiveData<List<ProductState>>(emptyList())

    private val historyItems = MutableLiveData<List<HistoryState>>(emptyList())

    private val loadState = MutableLiveData<LoadState>()

    private val _uiState =
        MediatorLiveData<ProductUiState>().apply {
            addSourceList(productItems, historyItems, loadState) { combine() }
        }
    val uiState: LiveData<ProductUiState> get() = _uiState

    private val _uiEvent = MutableSingleLiveData<MainUiEvent>()
    val uiEvent: SingleLiveData<MainUiEvent> get() = _uiEvent

    private val _isLoading = MutableSingleLiveData<Boolean>()
    val isLoading: SingleLiveData<Boolean> get() = _isLoading

    init {
        loadInitial()
    }

    private fun loadInitial() {
        handleLoading(true)
        defaultProductRepository.loadSinglePage(INITIAL_PAGE, PAGE_SIZE) { result ->
            result.fold(
                onSuccess = { paged ->
                    val productList: List<Product> = paged.products
                    val stateList = productList.map { product -> ProductState(product, Quantity(10)) }
                    productItems.postValue(stateList)
                },
                onFailure = { throwable ->
                    Log.e("ProductLoad", "Error loading products", throwable)
                },
            )
        }

        historyLoader { historyStates ->
            productWithCartLoader(INITIAL_PAGE, PAGE_SIZE) { productStates, hasNextPage ->
                historyItems.postValue(historyStates)
                productItems.postValue(productStates)
                loadState.postValue(LoadState.of(hasNextPage))
            }
            handleLoading(false)
        }
    }

    fun loadPage() {
        withState(productItems.value) { state ->
            val pageIndex = state.size / PAGE_SIZE
            productWithCartLoader(pageIndex, PAGE_SIZE) { newProducts, hasNext ->
                productItems.postValue(state + newProducts)
                loadState.postValue(LoadState.of(hasNext))
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
        withState(productItems.value) { state ->
            val productIds = state.map { it.item.id }

            cartRepository.getCarts(productIds) { carts ->
                val updated =
                    state.mapIndexed { i, productState ->
                        val quantity = carts.getOrNull(i)?.quantity ?: Quantity(0)
                        productState.copy(cartQuantity = quantity)
                    }
                productItems.postValue(updated)
            }
        }
    }

    fun syncHistory() {
        historyLoader { historyStates ->
            historyItems.postValue(historyStates)
        }
    }

    fun saveHistory(productId: Long) {
        withState(_uiState.value) { state ->
            historyRepository.saveHistory(productId) {
                _uiEvent.postValue(MainUiEvent.NavigateToDetail(productId, state.lastSeenProductId))
            }
        }
    }

    private fun handleLoading(isLoading: Boolean)  {
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

    private fun combine() {
        val products = productItems.value ?: return
        val history = historyItems.value ?: return
        val load = loadState.value ?: return

        _uiState.value =
            ProductUiState(
                productItems = products,
                historyItems = history,
                load = load,
            )
    }

    companion object {
        private const val INITIAL_PAGE = 0
        private const val PAGE_SIZE = 20
    }
}
