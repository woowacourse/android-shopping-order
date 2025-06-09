package woowacourse.shopping.view.main.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.cart.Cart
import woowacourse.shopping.domain.cart.ShoppingCarts
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.product.ProductSinglePage
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.HistoryRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.view.core.common.withState
import woowacourse.shopping.view.core.event.MutableSingleLiveData
import woowacourse.shopping.view.core.event.SingleLiveData
import woowacourse.shopping.view.main.MainUiEvent
import woowacourse.shopping.view.main.adapter.ProductAdapterEventHandler
import woowacourse.shopping.view.main.state.LoadState
import woowacourse.shopping.view.main.state.ProductState
import woowacourse.shopping.view.main.state.ProductState.Companion.NOT_IN_CART
import woowacourse.shopping.view.main.state.ProductUiState

class MainViewModel(
    private val historyRepository: HistoryRepository,
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
) : ViewModel() {
    private val _uiState = MutableLiveData(ProductUiState())
    val uiState: LiveData<ProductUiState> get() = _uiState

    private val _uiEvent = MutableSingleLiveData<MainUiEvent>()
    val uiEvent: SingleLiveData<MainUiEvent> get() = _uiEvent

    init {
        fetchProducts(INITIAL_PAGE)
        fetchCartQuantity()
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
            val cartResult = cartRepository.loadSinglePage(ALL_PAGE_INDEX, ALL_PAGE_SIZE)

            productResult.onSuccess { productPage ->
                cartResult.onSuccess { cartPage ->
                    applyMergedUiState(productPage, cartPage.carts)
                }.onFailure(::handleFailure)
            }
        }
    }

    private fun applyMergedUiState(
        productPage: ProductSinglePage,
        carts: ShoppingCarts,
    ) = withState(_uiState.value) { state ->
        val newProducts = generateProductStates(productPage, carts)
        viewModelScope.launch {
            val newHistory = generateHistoryProductStates()
            updateUiState {
                state.copy(
                    productItems = newProducts,
                    historyItems = newHistory,
                    load = LoadState.of(productPage.hasNextPage),
                    isFetching = false,
                )
            }
            toggleFetching()
        }
    }

    private fun fetchCartQuantity() {
        viewModelScope.launch {
            cartRepository.cartQuantity()
                .onSuccess { updateUiState { modifySumOfCartQuantity(it) } }
                .onFailure(::handleFailure)
        }
    }

    private suspend fun generateHistoryProductStates(): List<Product> {
        val historyProducts = mutableListOf<Product>()
        val histories = historyRepository.getHistories()
        if (histories.isEmpty()) return emptyList()

        histories.forEach {
            productRepository.loadProduct(it)
                .onSuccess { product -> historyProducts.add(product) }
                .onFailure(::handleFailure)
        }
        return historyProducts
    }

    private fun generateProductStates(
        productPage: ProductSinglePage,
        carts: ShoppingCarts,
    ): List<ProductState> {
        val newStates =
            productPage.products.map { product ->
                val cart = carts.findProduct(product.id)

                ProductState.of(cart, product)
            }
        return _uiState.value?.productItems.orEmpty() + newStates
    }

    fun increaseCartQuantity(productId: Long) =
        withState(_uiState.value) { state ->
            val updated = state.increaseCartQuantity(productId)
            val request = Cart(updated.cartQuantity, productId)
            viewModelScope.launch {
                if (updated.cartId == NOT_IN_CART) {
                    cartRepository.addCart(request)
                        .onSuccess { newId ->
                            updateUiState { modifyUiState(updated.copy(cartId = newId)) }
                        }.onFailure(::handleFailure)
                } else {
                    cartRepository.updateQuantity(updated.cartId, updated.cartQuantity)
                        .onSuccess {
                            updateUiState { modifyUiState(updated) }
                        }
                        .onFailure(::handleFailure)
                }
                fetchCartQuantity()
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
                            updateUiState { modifyUiState(updated.copy(cartId = NOT_IN_CART)) }
                        }.onFailure(::handleFailure)
                }
                fetchCartQuantity()
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
                cartRepository.loadSinglePage(ALL_PAGE_INDEX, ALL_PAGE_SIZE)
                    .onSuccess { result ->
                        updateUiState { modifyQuantity(result.carts) }
                    }.onFailure(::handleFailure)
            }
        }

    private fun navigateToDetail(productId: Long) =
        withState(_uiState.value) { state ->
            _uiEvent.setValue(MainUiEvent.NavigateToDetail(productId, state.lastSeenProductId))
        }

    fun handleNavigateToCart() =
        withState(_uiState.value) { state ->
            _uiEvent.setValue(MainUiEvent.NavigateToCart(state.lastSeenProductCategory))
        }

    private fun toggleFetching() {
        _uiState.value = _uiState.value?.fetchToggleState()
    }

    private fun updateUiState(transform: ProductUiState.() -> ProductUiState) {
        _uiState.value = _uiState.value?.let(transform)
    }

    private fun handleFailure(throwable: Throwable) {
        _uiEvent.setValue(MainUiEvent.ShowErrorMessage(throwable))
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
        private val ALL_PAGE_INDEX = null
        private val ALL_PAGE_SIZE = null
    }
}
