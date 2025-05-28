package woowacourse.shopping.view.main.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.repository.DefaultCartRepository
import woowacourse.shopping.data.repository.DefaultProductRepository
import woowacourse.shopping.domain.Quantity
import woowacourse.shopping.domain.cart.Cart
import woowacourse.shopping.domain.cart.ShoppingCart
import woowacourse.shopping.domain.product.ProductSinglePage
import woowacourse.shopping.domain.repository.HistoryRepository
import woowacourse.shopping.view.core.common.withState
import woowacourse.shopping.view.core.event.MutableSingleLiveData
import woowacourse.shopping.view.core.event.SingleLiveData
import woowacourse.shopping.view.loader.HistoryLoader
import woowacourse.shopping.view.main.MainUiEvent
import woowacourse.shopping.view.main.state.LoadState
import woowacourse.shopping.view.main.state.ProductState
import woowacourse.shopping.view.main.state.ProductUiState

class MainViewModel(
    private val historyRepository: HistoryRepository,
    private val historyLoader: HistoryLoader,
    private val productRepository: DefaultProductRepository,
    private val cartRepository: DefaultCartRepository,
) : ViewModel() {
    private val _uiState = MutableLiveData<ProductUiState>()
    val uiState: LiveData<ProductUiState> get() = _uiState

    private val _uiEvent = MutableSingleLiveData<MainUiEvent>()
    val uiEvent: SingleLiveData<MainUiEvent> get() = _uiEvent

    private val _isLoading = MutableSingleLiveData(true)
    val isLoading: SingleLiveData<Boolean> get() = _isLoading

    init {
        loadProductsAndCarts(INITIAL_PAGE)
    }

    fun loadPage() =
        withState(_uiState.value) { state ->
            val nextPageIndex = state.productItems.size / PAGE_SIZE
            loadProductsAndCarts(nextPageIndex)
        }

    private fun loadProductsAndCarts(pageIndex: Int) {
        setLoading(true)
        productRepository.loadSinglePage(pageIndex, PAGE_SIZE) { productResult ->
            productResult.fold(
                onSuccess = { productPage ->
                    loadCartsAndMerge(productPage, pageIndex)
                },
                onFailure = { handleError("ProductLoad", it) },
            )
        }
    }

    private fun loadCartsAndMerge(
        productPage: ProductSinglePage,
        pageIndex: Int,
    ) {
        cartRepository.loadSinglePage(pageIndex, PAGE_SIZE) { cartResult ->
            cartResult.fold(
                onSuccess = { cartPage ->
                    applyMergedUiState(productPage, cartPage.carts)
                },
                onFailure = { handleError("CartLoad", it) },
            )
        }
    }

    private fun applyMergedUiState(
        productPage: ProductSinglePage,
        cartItems: List<ShoppingCart>,
    ) {
        val newStates =
            productPage.products.map { product ->
                val cartItem = cartItems.find { it.product.id == product.id }
                ProductState(
                    cartId = cartItem?.id,
                    item = product,
                    cartQuantity = cartItem?.quantity ?: Quantity(0),
                )
            }

        val updatedList = _uiState.value?.productItems.orEmpty() + newStates
        _uiState.value = ProductUiState(updatedList, load = LoadState.of(productPage.hasNextPage))

        setLoading(false)
    }

    fun increaseCartQuantity(productId: Long) =
        withState(_uiState.value) { state ->
            val updated = state.increaseCartQuantity(productId)

            val updateUiState = { _uiState.value = state.modifyUiState(updated) }

            when (val cartId = updated.cartId) {
                null -> {
                    cartRepository.addCart(Cart(updated.cartQuantity, productId)) {
                        it.fold(
                            onSuccess = { updateUiState() },
                            onFailure = { handleError(TAG_INCREASE, it) },
                        )
                    }
                }

                else -> {
                    cartRepository.updateQuantity(cartId, updated.cartQuantity) {
                        it.fold(
                            onSuccess = { updateUiState() },
                            onFailure = { handleError(TAG_INCREASE, it) },
                        )
                    }
                }
            }
        }

    fun decreaseCartQuantity(productId: Long) =
        withState(_uiState.value) { state ->
            val updated = state.decreaseCartQuantity(productId)
            val cartId = updated.cartId ?: return

            val updateUiState = { _uiState.value = state.modifyUiState(updated) }

            if (updated.hasCartQuantity) {
                cartRepository.updateQuantity(cartId, updated.cartQuantity) {
                    it.fold(
                        onSuccess = { updateUiState() },
                        onFailure = { handleError(TAG_DECREASE, it) },
                    )
                }
            } else {
                cartRepository.deleteCart(cartId) {
                    it.fold(
                        onSuccess = { updateUiState() },
                        onFailure = { handleError(TAG_DECREASE, it) },
                    )
                }
            }
        }

    fun syncCartQuantities() =
        withState(_uiState.value) { state ->
            // TODO: cartRepository.getCarts(state.productIds) { ... }
        }

    fun syncHistory() {
        historyLoader { historyStates ->
            // TODO: implement UI 반영
        }
    }

    fun saveHistory(productId: Long) =
        withState(_uiState.value) { state ->
            historyRepository.saveHistory(productId) {
                _uiEvent.postValue(MainUiEvent.NavigateToDetail(productId, state.lastSeenProductId))
            }
        }

    private fun setLoading(isLoading: Boolean) {
        _isLoading.postValue(isLoading)
    }

    private fun handleError(
        tag: String,
        throwable: Throwable,
    ) {
        Log.e(tag, "Error occurred", throwable)
        setLoading(false)
    }

    companion object {
        private const val INITIAL_PAGE = 0
        private const val PAGE_SIZE = 20

        private const val TAG_INCREASE = "ProductQuantityIncrease"
        private const val TAG_DECREASE = "ProductQuantityDecrease"
    }
}
