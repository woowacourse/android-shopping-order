package woowacourse.shopping.view.cart.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.Quantity
import woowacourse.shopping.domain.cart.Cart
import woowacourse.shopping.domain.exception.onFailure
import woowacourse.shopping.domain.exception.onSuccess
import woowacourse.shopping.domain.product.ProductSinglePage
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.view.cart.CartUiEvent
import woowacourse.shopping.view.cart.carts.CartAdapter
import woowacourse.shopping.view.cart.recommend.RecommendAdapter
import woowacourse.shopping.view.cart.state.CartState
import woowacourse.shopping.view.cart.state.CartUiState
import woowacourse.shopping.view.cart.state.RecommendUiState
import woowacourse.shopping.view.cart.vm.Paging.Companion.INITIAL_PAGE_NO
import woowacourse.shopping.view.cart.vm.Paging.Companion.PAGE_SIZE
import woowacourse.shopping.view.core.common.withState
import woowacourse.shopping.view.core.event.MutableSingleLiveData
import woowacourse.shopping.view.core.event.SingleLiveData
import woowacourse.shopping.view.core.handler.CartQuantityHandler
import woowacourse.shopping.view.main.state.ProductState

class CartViewModel(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
) : ViewModel() {
    private val paging = Paging(initialPage = INITIAL_PAGE_NO, pageSize = PAGE_SIZE)

    private val _cartUiState = MutableLiveData<CartUiState>()
    val cartUiState: LiveData<CartUiState> get() = _cartUiState

    private val _recommendUiState = MutableLiveData<RecommendUiState>()
    val recommendUiState: LiveData<RecommendUiState> get() = _recommendUiState

    private val _uiEvent = MutableSingleLiveData<CartUiEvent>()
    val uiEvent: SingleLiveData<CartUiEvent> get() = _uiEvent

    private var lastSeenCategory: String? = null

    init {
        loadCarts()
    }

    fun setCategory(category: String?) {
        lastSeenCategory = category
    }

    fun loadRecommendProduct() {
        viewModelScope.launch {
            productRepository.loadSinglePage(lastSeenCategory, null, null)
                .onSuccess { result ->
                    val products = extractRecommendProduct(result)
                    _recommendUiState.value = RecommendUiState(products)
                }.onFailure(::handleFailure)
        }
    }

    private fun extractRecommendProduct(page: ProductSinglePage): List<ProductState> {
        val currentIds = _cartUiState.value?.cartIds ?: emptyList()
        return page
            .products
            .asSequence()
            .filter { it.id !in currentIds }
            .shuffled()
            .take(RECOMMEND_SIZE)
            .map { ProductState(item = it, cartQuantity = Quantity(0)) }
            .toList()
    }

    fun increaseRecommendProductQuantity(productId: Long) =
        withState(_recommendUiState.value) { state ->
            val updated = state.increaseQuantity(productId)
            viewModelScope.launch {
                when (val cartId = updated.cartId) {
                    null -> {
                        cartRepository.addCart(Cart(updated.cartQuantity, productId))
                            .onSuccess { value ->
                                val cartId = value
                                _recommendUiState.value =
                                    state.modifyUiState(updated.copy(cartId = cartId))
                                _cartUiState.value = _cartUiState.value?.addCart(cartId, updated)
                            }
                            .onFailure(::handleFailure)
                    }

                    else -> {
                        cartRepository.updateQuantity(cartId, updated.cartQuantity) { value ->
                            value
                                .onSuccess {
                                    _recommendUiState.value = state.modifyUiState(updated)
                                    increaseCartQuantity(cartId)
                                }
                                .onFailure(::handleFailure)
                        }
                    }
                }
            }
        }

    fun decreaseRecommendProductQuantity(productId: Long) =
        withState(_recommendUiState.value) { state ->
            val updated = state.decreaseCartQuantity(productId)
            val cartId = updated.cartId ?: return

            if (updated.hasCartQuantity) {
                cartRepository.updateQuantity(cartId, updated.cartQuantity) {
                    it.onSuccess {
                        _recommendUiState.value = state.modifyUiState(updated)
                        decreaseCartQuantity(cartId)
                    }
                        .onFailure(::handleFailure)
                }
            } else {
                cartRepository.deleteCart(cartId) {
                    val result = updated.copy(cartId = null)
                    _recommendUiState.value = state.modifyUiState(result)
                    _cartUiState.value = _cartUiState.value?.deleteCart(cartId)
                }
            }
        }

    fun decreaseCartQuantity(cartId: Long) {
        withState(_cartUiState.value) { state ->
            val updatedState = state.decreaseCartQuantity(cartId)
            val updatedItem = updatedState.items.first { it.cartId == cartId }

            cartRepository.updateQuantity(cartId, updatedItem.cart.quantity) { result ->
                result
                    .onSuccess { _cartUiState.value = updatedState }
                    .onFailure(::handleFailure)
            }
        }
    }

    fun increaseCartQuantity(cartId: Long) {
        withState(_cartUiState.value) { state ->
            val updatedState = state.increaseCartQuantity(cartId)
            val updatedItem = updatedState.findCart(cartId)

            cartRepository.updateQuantity(cartId, updatedItem.cart.quantity) { result ->
                result
                    .onSuccess { _cartUiState.value = updatedState }
                    .onFailure(::handleFailure)
            }
        }
    }

    fun loadCarts() {
        toggleFetching()
        val nextPage = paging.getPageNo() - 1
        cartRepository.loadSinglePage(
            nextPage,
            PAGE_SIZE,
        ) { result ->

            result
                .onSuccess { value ->
                    val pageState = paging.createPageState(!value.hasNextPage)
                    val newItems =
                        value
                            .carts
                            .map { CartState(it, _cartUiState.value?.allChecked ?: false) }

                    val currentItems = _cartUiState.value?.items ?: emptyList()
                    val combinedItems = currentItems + newItems

                    _cartUiState.value = CartUiState(items = combinedItems, pageState = pageState)
                }
                .onFailure(::handleFailure)
            toggleFetching()
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
        withState(_cartUiState.value) { state ->
            _cartUiState.value = state.modifyCheckedState(cartId, isChecked)
        }
    }

    fun changeAllStateChecked(isChecked: Boolean) {
        withState(_cartUiState.value) { state ->
            _cartUiState.value = state.setAllItemsChecked(isChecked)
        }
    }

    private fun refresh() {
        withState(_cartUiState.value) { state ->
            cartRepository.loadSinglePage(paging.getPageNo() - 1, PAGE_SIZE) { result ->
                result.onSuccess { value ->

                    if (paging.resetToLastPageIfEmpty(value.carts)) {
                        refresh()
                        return@loadSinglePage
                    }

                    val pageState = paging.createPageState(!value.hasNextPage)
                    val carts = value.carts.map { CartState(it, state.allChecked) }

                    _cartUiState.value = CartUiState(items = carts, pageState = pageState)
                }
                    .onFailure(::handleFailure)
            }
        }
    }

    private fun toggleFetching() {
        withState(_cartUiState.value) { state ->
            _cartUiState.value = state.toggleFetching()
        }
    }

    fun onChangeScreen() {
        _uiEvent.setValue(CartUiEvent.ChangeScreen)
    }

    private fun handleFailure(throwable: Throwable) {
        _uiEvent.setValue(CartUiEvent.ShowErrorMessage(throwable))
    }

    val recommendEventHandler =
        object : RecommendAdapter.Handler {
            override fun showQuantity(productId: Long) {
                increaseRecommendProductQuantity(productId)
            }
        }

    val quantityEventHandler =
        object : CartQuantityHandler {
            override fun onClickIncrease(productId: Long) {
                increaseRecommendProductQuantity(productId)
            }

            override fun onClickDecrease(productId: Long) {
                decreaseRecommendProductQuantity(productId)
            }
        }

    val cartQuantityEventHandler =
        object : CartQuantityHandler {
            override fun onClickIncrease(cartId: Long) {
                increaseCartQuantity(cartId)
            }

            override fun onClickDecrease(cartId: Long) {
                decreaseCartQuantity(cartId)
            }
        }

    val cartEventHandler =
        object : CartAdapter.Handler {
            override fun onClickDeleteItem(cartId: Long) {
                deleteProduct(cartId)
            }

            override fun onCheckedChanged(
                cartId: Long,
                isChecked: Boolean,
            ) {
                updateCheckedState(cartId, isChecked)
            }
        }

    companion object {
        private const val RECOMMEND_SIZE = 10
    }
}
