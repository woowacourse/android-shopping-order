package woowacourse.shopping.view.main.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.domain.Quantity
import woowacourse.shopping.domain.cart.Cart
import woowacourse.shopping.domain.cart.ShoppingCart
import woowacourse.shopping.domain.product.ProductSinglePage
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.view.core.common.withState
import woowacourse.shopping.view.core.event.MutableSingleLiveData
import woowacourse.shopping.view.core.event.SingleLiveData
import woowacourse.shopping.view.loader.HistoryLoader
import woowacourse.shopping.view.main.MainUiEvent
import woowacourse.shopping.view.main.adapter.ProductAdapterEventHandler
import woowacourse.shopping.view.main.state.LoadState
import woowacourse.shopping.view.main.state.ProductState
import woowacourse.shopping.view.main.state.ProductUiState

class MainViewModel(
    private val historyLoader: HistoryLoader,
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
) : ViewModel() {
    private val _uiState = MutableLiveData<ProductUiState>()
    val uiState: LiveData<ProductUiState> get() = _uiState

    private val _uiEvent = MutableSingleLiveData<MainUiEvent>()
    val uiEvent: SingleLiveData<MainUiEvent> get() = _uiEvent

    init {
        loadProductsAndCarts(INITIAL_PAGE)
    }

    fun loadPage() =
        withState(_uiState.value) { state ->
            val nextPageIndex = state.productItemSize / PAGE_SIZE
            loadProductsAndCarts(nextPageIndex)
        }

    private fun loadProductsAndCarts(pageIndex: Int) {
        toggleFetching()
        productRepository.loadSinglePage(page = pageIndex, pageSize = PAGE_SIZE) { productResult ->
            productResult
                .onSuccess { loadCartsAndMerge(it, pageIndex) }
                .onFailure(::handleFailure)
        }
    }

    private fun loadCartsAndMerge(
        productPage: ProductSinglePage,
        pageIndex: Int,
    ) {
        cartRepository.loadSinglePage(pageIndex, PAGE_SIZE) {
            it
                .onSuccess { result -> applyMergedUiState(productPage, result.carts) }
                .onFailure(::handleFailure)
        }
    }

    private fun applyMergedUiState(
        productPage: ProductSinglePage,
        cartItems: List<ShoppingCart>,
    ) {
        historyLoader { result ->
            result.onSuccess { historyStates ->

                val updatedList = _uiState.value?.productItems.orEmpty() + newStates

                _uiState.postValue(
                    ProductUiState(
                        productItems = updatedList,
                        historyItems = historyStates,
                        load = LoadState.of(productPage.hasNextPage),
                    ),
                )

                toggleFetching()
            }
                    val newStates =
                        productPage
                            .products
                            .map { product ->
                                val cartItem = cartItems.find { it.productId == product.id }
                                ProductState.of(cartItem, product)
                            }
                .onFailure(::handleFailure)
        }
    }

    fun increaseCartQuantity(productId: Long) =
        withState(_uiState.value) { state ->
            val updated = state.increaseCartQuantity(productId)

            when (val cartId = updated.cartId) {
                null -> {
                    cartRepository.addCart(Cart(updated.cartQuantity, productId)) {
                        it.onSuccess { value ->
                            _uiState.value = state.modifyUiState(updated.copy(value?.toLong()))
                        }
                            .onFailure(::handleFailure)
                    }
                }

                else -> {
                    cartRepository.updateQuantity(cartId, updated.cartQuantity) {
                        _uiState.value = state.modifyUiState(updated)
                    }
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
                        .onSuccess { _uiState.value = state.modifyUiState(updated) }
                        .onFailure(::handleFailure)
                }
            } else {
                cartRepository.deleteCart(cartId) {
                    it.onSuccess {
                        val result = updated.copy(cartId = null)
                        _uiState.value = state.modifyUiState(result)
                    }
                        .onFailure(::handleFailure)
                }
            }
        }

    fun syncHistory() {
        withState(_uiState.value) { state ->
            historyLoader { historyStates ->
                historyStates
                    .onSuccess { _uiState.value = state.copy(historyItems = it) }
                    .onFailure(::handleFailure)
            }
        }
    }

    fun syncCartQuantities() =
        withState(_uiState.value) { state ->
            cartRepository.loadSinglePage(null, null) {
                it
                    .onSuccess {
                        _uiState.value = state.modifyQuantity(it?.carts.orEmpty())
                    }
                    .onFailure(::handleFailure)
            }
        }

    fun handleNavigateDetailEvent(productId: Long) {
        withState(_uiState.value) { state ->
            _uiEvent.postValue(MainUiEvent.NavigateToDetail(productId, state.lastSeenProductId))
        }
    }

    fun handleNavigateToCart() {
        withState(_uiState.value) { uiState ->
            _uiEvent.postValue(MainUiEvent.NavigateToCart(uiState.lastSeenProductCategory))
        }
    }

    private fun toggleFetching() {
        withState(_uiState.value) { state ->
            _uiState.value = state.toggleFetching()
        }
    }

    private fun handleFailure(throwable: Throwable) {
        _uiEvent.setValue(MainUiEvent.ShowErrorMessage(throwable))
    }

    val productEventHandler =
        object : ProductAdapterEventHandler {
            override fun onLoadMoreItems() {
                loadPage()
            }

            override fun onSelectProduct(productId: Long) {
                handleNavigateDetailEvent(productId)
            }

            override fun showQuantity(productId: Long) {
                increaseCartQuantity(productId)
            }

            override fun onClickHistory(productId: Long) {
                handleNavigateDetailEvent(productId)
            }

            override fun onClickIncrease(cartId: Long) {
                increaseCartQuantity(cartId)
            }

            override fun onClickDecrease(cartId: Long) {
                decreaseCartQuantity(cartId)
            }
        }

    companion object {
        private const val INITIAL_PAGE = 0
        private const val PAGE_SIZE = 20
    }
}
