package woowacourse.shopping.view.cart.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.Quantity
import woowacourse.shopping.domain.cart.Cart
import woowacourse.shopping.domain.cart.ShoppingCarts
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
import woowacourse.shopping.view.core.common.withState
import woowacourse.shopping.view.core.event.MutableSingleLiveData
import woowacourse.shopping.view.core.event.SingleLiveData
import woowacourse.shopping.view.core.handler.CartQuantityHandler
import woowacourse.shopping.view.main.state.ProductState
import woowacourse.shopping.view.main.state.ProductState.Companion.NOT_IN_CART

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
            productRepository.loadSinglePage(lastSeenCategory, ALL_PAGE_INDEX, ALL_PAGE_SIZE)
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
                        cartRepository.updateQuantity(cartId, updated.cartQuantity)
                            .onSuccess {
                                _recommendUiState.value = state.modifyUiState(updated)
                                increaseCartQuantity(cartId)
                            }
                            .onFailure(::handleFailure)
                    }
                }
            }
        }

    fun decreaseRecommendProductQuantity(productId: Long) =
        withState(_recommendUiState.value) { state ->
            val updated = state.decreaseCartQuantity(productId)
            val cartId = updated.cartId ?: return
            viewModelScope.launch {
                if (updated.hasCartQuantity) {
                    cartRepository.updateQuantity(cartId, updated.cartQuantity)
                        .onSuccess {
                            _recommendUiState.value = state.modifyUiState(updated)
                            decreaseCartQuantity(cartId)
                        }.onFailure(::handleFailure)
                } else {
                    cartRepository.deleteCart(cartId).onSuccess {
                        val result = updated.copy(cartId = NOT_IN_CART)
                        _recommendUiState.value = state.modifyUiState(result)
                        _cartUiState.value = _cartUiState.value?.deleteCart(cartId)
                    }.onFailure(::handleFailure)
                }
            }
        }

    fun decreaseCartQuantity(cartId: Long) {
        withState(_cartUiState.value) { state ->
            val updatedState = state.decreaseCartQuantity(cartId)
            val updatedItem = updatedState.items.first { it.cartId == cartId }

            viewModelScope.launch {
                cartRepository.updateQuantity(cartId, updatedItem.cart.quantity)
                    .onSuccess { _cartUiState.value = updatedState }
                    .onFailure(::handleFailure)
            }
        }
    }

    fun increaseCartQuantity(cartId: Long) {
        withState(_cartUiState.value) { state ->
            val updatedState = state.increaseCartQuantity(cartId)
            val updatedItem = updatedState.findCart(cartId)

            viewModelScope.launch {
                cartRepository.updateQuantity(cartId, updatedItem.cart.quantity)
                    .onSuccess { _cartUiState.value = updatedState }
                    .onFailure(::handleFailure)
            }
        }
    }

    private fun loadCarts() {
        toggleFetching()
        val nextPage = paging.getPageNo() - 1
        viewModelScope.launch {
            cartRepository.loadSinglePage(
                nextPage,
                PAGE_SIZE,
            )
                .onSuccess { value ->
                    val pageState = paging.createPageState(!value.hasNextPage)
                    val newItems =
                        value
                            .carts
                            .shoppingCarts
                            .map { CartState(it, _cartUiState.value?.allChecked ?: false) }

                    val currentItems = _cartUiState.value?.items.orEmpty()
                    val combinedItems = currentItems + newItems

                    _cartUiState.value =
                        CartUiState(items = combinedItems, pageState = pageState)
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
        viewModelScope.launch {
            cartRepository.deleteCart(productId)
                .onSuccess { refresh() }
                .onFailure(::handleFailure)
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
            viewModelScope.launch {
                cartRepository.loadSinglePage(paging.getPageNo() - 1, PAGE_SIZE)
                    .onSuccess { value ->

                        if (paging.resetToLastPageIfEmpty(value.carts.shoppingCarts)) {
                            refresh()
                            return@onSuccess
                        }

                        val pageState = paging.createPageState(!value.hasNextPage)
                        val carts =
                            value.carts.shoppingCarts.map { CartState(it, state.allChecked) }

                        _cartUiState.value =
                            CartUiState(items = carts, pageState = pageState)
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

    fun sendScreenChangeEvent() {
        withState(_cartUiState.value) { state ->
            if (!state.hasPurchaseCart) {
                _uiEvent.setValue(CartUiEvent.ShowNotHasPurchaseCart)
            } else {
                _uiEvent.setValue(CartUiEvent.ChangeScreen(ShoppingCarts(state.purchaseCart)))
            }
        }
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
        private const val PAGE_SIZE = 5
        private val ALL_PAGE_INDEX = null
        private val ALL_PAGE_SIZE = null
    }
}
