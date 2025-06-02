package woowacourse.shopping.view.main.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.domain.cart.Cart
import woowacourse.shopping.domain.cart.ShoppingCart
import woowacourse.shopping.domain.product.ProductSinglePage
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.view.core.common.withState
import woowacourse.shopping.view.core.event.MutableSingleLiveData
import woowacourse.shopping.view.core.event.SingleLiveData
import woowacourse.shopping.view.core.ext.addSourceList
import woowacourse.shopping.view.loader.HistoryLoader
import woowacourse.shopping.view.main.MainUiEvent
import woowacourse.shopping.view.main.adapter.ProductAdapterEventHandler
import woowacourse.shopping.view.main.state.HistoryState
import woowacourse.shopping.view.main.state.LoadState
import woowacourse.shopping.view.main.state.ProductState
import woowacourse.shopping.view.main.state.ProductUiState

class MainViewModel(
    private val historyLoader: HistoryLoader,
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
        loadPage()
    }

    fun loadPage() =
        withState(_uiState.value) { state ->
            val nextPageIndex = state.productItemSize / PAGE_SIZE
            fetchProducts(nextPageIndex)
        }

    private fun fetchProducts(pageIndex: Int) {
        productRepository.loadSinglePage(page = pageIndex, pageSize = PAGE_SIZE) { result ->
            result.onSuccess { productPage ->
                cartRepository.loadSinglePage(pageIndex, PAGE_SIZE) { cartResult ->
                    cartResult.onSuccess { cartPage ->
                        applyMergedUiState(productPage, cartPage.carts)
                    }.onFailure(::handleFailure)
                }
            }.onFailure(::handleFailure)
        }
    }

    private fun applyMergedUiState(
        productPage: ProductSinglePage,
        carts: List<ShoppingCart>,
    ) {
        val newProducts = generateProductStates(productPage, carts)
        historyLoader { result ->
            result.onSuccess {
                historyItems.postValue(it)
                productItems.postValue(newProducts)
                loadState.postValue(LoadState.of(productPage.hasNextPage))
            }.onFailure(::handleFailure)
        }
        toggleFetching()
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

            if (updated.cartId == null) {
                cartRepository.addCart(request) {
                    it.onSuccess { newId ->
                        updateUiState { modifyUiState(updated.copy(cartId = newId.toLong())) }
                    }.onFailure(::handleFailure)
                }
            } else {
                cartRepository.updateQuantity(updated.cartId, updated.cartQuantity) {
                    updateUiState { modifyUiState(updated) }
                }
            }
        }

    fun decreaseCartQuantity(productId: Long) =
        withState(_uiState.value) { state ->
            val updated = state.decreaseCartQuantity(productId)
            val cartId = updated.cartId ?: return

            if (updated.hasCartQuantity) {
                cartRepository.updateQuantity(cartId, updated.cartQuantity) {
                    it
                        .onSuccess { updateUiState { modifyUiState(updated) } }
                        .onFailure(::handleFailure)
                }
            } else {
                cartRepository.deleteCart(cartId) {
                    it
                        .onSuccess {
                            updateUiState { modifyUiState(updated.copy(cartId = null)) }
                        }.onFailure(::handleFailure)
                }
            }
        }

    fun syncHistory() =
        withState(_uiState.value) {
            historyLoader {
                it.onSuccess { history ->
                    updateUiState { copy(historyItems = history) }
                }.onFailure(::handleFailure)
            }
        }

    fun syncCartQuantities() =
        withState(_uiState.value) {
            cartRepository.loadSinglePage(null, null) {
                it.onSuccess { result ->
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
