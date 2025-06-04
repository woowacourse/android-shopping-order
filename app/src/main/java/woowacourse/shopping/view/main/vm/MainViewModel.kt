package woowacourse.shopping.view.main.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.cart.Cart
import woowacourse.shopping.domain.cart.ShoppingCart
import woowacourse.shopping.domain.exception.NetworkError
import woowacourse.shopping.domain.exception.onFailure
import woowacourse.shopping.domain.exception.onSuccess
import woowacourse.shopping.domain.product.ProductSinglePage
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.HistoryRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.view.core.common.withState
import woowacourse.shopping.view.core.event.MutableSingleLiveData
import woowacourse.shopping.view.core.event.SingleLiveData
import woowacourse.shopping.view.core.ext.addSourceList
import woowacourse.shopping.view.main.MainUiEvent
import woowacourse.shopping.view.main.adapter.ProductAdapterEventHandler
import woowacourse.shopping.view.main.state.HistoryState
import woowacourse.shopping.view.main.state.LoadState
import woowacourse.shopping.view.main.state.ProductState
import woowacourse.shopping.view.main.state.ProductUiState

class MainViewModel(
    private val historyRepository: HistoryRepository,
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
) : ViewModel() {
    private val productItems = MutableLiveData<List<ProductState>>(emptyList())
    private val historyItems = MutableLiveData<List<HistoryState>>(emptyList())
    private val loadState = MutableLiveData<LoadState>()

    private val _uiState =
        MediatorLiveData(ProductUiState()).apply {
            addSourceList(productItems, historyItems, loadState) { combine() }
        }
    val uiState: LiveData<ProductUiState> get() = _uiState

    private val _uiEvent = MutableSingleLiveData<MainUiEvent>()
    val uiEvent: SingleLiveData<MainUiEvent> get() = _uiEvent

    init {
        fetchProducts(0)
    }

    fun loadPage() =
        withState(_uiState.value) { state ->
            val nextPageIndex = state.productItemSize / PAGE_SIZE
            fetchProducts(nextPageIndex)
        }

    private fun fetchProducts(pageIndex: Int) {
        viewModelScope.launch {
            val productResult =
                productRepository.loadSinglePage(page = pageIndex, pageSize = PAGE_SIZE)
            val cartResult = cartRepository.loadSinglePage(pageIndex, PAGE_SIZE)

            productResult.onSuccess { productPage ->
                cartResult.onSuccess { cartPage ->
                    applyMergedUiState(productPage, cartPage.carts)
                }.onFailure(::handleFailure)
            }.onFailure(::handleFailure)
        }
    }

    private fun applyMergedUiState(
        productPage: ProductSinglePage,
        carts: List<ShoppingCart>,
    ) {
        val newProducts = generateProductStates(productPage, carts)
        viewModelScope.launch {
            val newHistory = generateHistoryProductStates()

            historyItems.value = newHistory
            productItems.value = newProducts
            loadState.value = LoadState.of(productPage.hasNextPage)

            toggleFetching()
        }
    }

    private suspend fun generateHistoryProductStates(): List<HistoryState> {
        val historyProducts = mutableListOf<HistoryState>()
        val histories = historyRepository.getHistories()
        if (histories.isEmpty()) return emptyList()

        histories.forEach {
            productRepository.loadProduct(it)
                .onSuccess { product ->
                    val historyState =
                        HistoryState(
                            product.id,
                            product.name,
                            product.category,
                            product.imgUrl,
                        )
                    historyProducts.add(historyState)
                }
                .onFailure(::handleFailure)
        }
        return historyProducts
    }

    private fun generateProductStates(
        productPage: ProductSinglePage,
        carts: List<ShoppingCart>,
    ): List<ProductState> {
        val newStates =
            productPage.products.map { product ->
                val cart = carts.find { it.productId == product.id }
                ProductState.of(cart, product)
            }
        return _uiState.value?.productItems.orEmpty() + newStates
    }

    fun increaseCartQuantity(productId: Long) =
        withState(_uiState.value) { state ->
            val updated = state.increaseCartQuantity(productId)
            val request = Cart(updated.cartQuantity, productId)
            viewModelScope.launch {
                if (updated.cartId == null) {
                    cartRepository.addCart(request)
                        .onSuccess { newId ->
                            updateUiState { modifyUiState(updated.copy(cartId = newId)) }
                        }.onFailure(::handleFailure)
                } else {
                    cartRepository.updateQuantity(updated.cartId, updated.cartQuantity)
                        .onSuccess { updateUiState { modifyUiState(updated) } }
                        .onFailure(::handleFailure)
                }
            }
        }

    fun decreaseCartQuantity(productId: Long) =
        withState(_uiState.value) { state ->
            val updated = state.decreaseCartQuantity(productId)
            val cartId = updated.cartId ?: return

            viewModelScope.launch {
                if (updated.hasCartQuantity) {
                    cartRepository.updateQuantity(cartId, updated.cartQuantity)
                        .onSuccess { updateUiState { modifyUiState(updated) } }
                        .onFailure(::handleFailure)
                } else {
                    cartRepository.deleteCart(cartId)
                        .onSuccess {
                            updateUiState { modifyUiState(updated.copy(cartId = null)) }
                        }.onFailure(::handleFailure)
                }
            }
        }

    fun syncHistory() =
        withState(_uiState.value) {
            viewModelScope.launch {
                val histories = generateHistoryProductStates()
                updateUiState { copy(historyItems = histories) }
            }
        }

    fun syncCartQuantities() =
        withState(_uiState.value) {
            viewModelScope.launch {
                cartRepository.loadSinglePage(null, null)
                    .onSuccess { result ->
                        updateUiState { modifyQuantity(result.carts) }
                    }.onFailure(::handleFailure)
            }
        }

    private fun navigateToDetail(productId: Long) =
        withState(_uiState.value) { state ->
            _uiEvent.postValue(MainUiEvent.NavigateToDetail(productId, state.lastSeenProductId))
        }

    fun handleNavigateToCart() =
        withState(_uiState.value) { state ->
            _uiEvent.postValue(MainUiEvent.NavigateToCart(state.lastSeenProductCategory))
        }

    private fun toggleFetching() {
        _uiState.postValue(_uiState.value?.fetchToggleState())
    }

    private fun updateUiState(transform: ProductUiState.() -> ProductUiState) {
        _uiState.value = _uiState.value?.let(transform)
    }

    private fun handleFailure(throwable: Throwable) {
        // _uiEvent.setValue(MainUiEvent.ShowErrorMessage(throwable))
    }

    private fun handleFailure(throwable: NetworkError) {
        _uiEvent.setValue(MainUiEvent.ShowErrorMessage(throwable))
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

    val productEventHandler =
        object : ProductAdapterEventHandler {
            override fun onLoadMoreItems() = loadPage()

            override fun onSelectProduct(productId: Long) = navigateToDetail(productId)

            override fun showQuantity(productId: Long) = increaseCartQuantity(productId)

            override fun onClickHistory(productId: Long) = navigateToDetail(productId)

            override fun onClickIncrease(cartId: Long) = increaseCartQuantity(cartId)

            override fun onClickDecrease(cartId: Long) = decreaseCartQuantity(cartId)
        }

    companion object {
        private const val INITIAL_PAGE = 0
        private const val PAGE_SIZE = 20
    }
}
