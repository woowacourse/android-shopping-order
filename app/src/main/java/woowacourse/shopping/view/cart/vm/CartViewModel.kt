package woowacourse.shopping.view.cart.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import woowacourse.shopping.domain.Quantity
import woowacourse.shopping.domain.cart.Cart
import woowacourse.shopping.domain.cart.ShoppingCart
import woowacourse.shopping.domain.product.Product
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

    private val _event = MutableSingleLiveData<CartUiEvent>()
    val event: SingleLiveData<CartUiEvent> get() = _event

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private var lastSeenCategory: String? = null

    init {
        loadCarts()
    }

    fun setCategory(category: String?) {
        lastSeenCategory = category
    }

    fun loadRecommendProduct() {
        viewModelScope.launch(Dispatchers.IO) {
            productRepository.loadSinglePage(lastSeenCategory, null, null)
                .onSuccess { result ->
                    val recommendProduct =
                        generateRecommendedProducts(result.products, _cartUiState.value?.cartIds ?: emptyList(), RECOMMEND_SIZE)
                    _recommendUiState.postValue(RecommendUiState(recommendProduct))
                }
        }
    }

    fun generateRecommendedProducts(
        allProducts: List<Product>,
        cartIds: List<Long>,
        recommendSize: Int,
    ): List<ProductState> {
        return allProducts
            .asSequence()
            .filter { it.id !in cartIds }
            .shuffled()
            .take(recommendSize)
            .map { ProductState(item = it, cartQuantity = Quantity(0)) }
            .toList()
    }

    fun increaseRecommendProductQuantity(productId: Long) =
        withState(_recommendUiState.value) { state ->
            val updated = state.increaseQuantity(productId)

            when (val cartId = updated.cartId) {
                null -> {
                    viewModelScope.launch(Dispatchers.IO) {
                        cartRepository.addCart(Cart(updated.cartQuantity, productId))
                            .onSuccess {
                                val cartId = cartId ?: 0L
                                _recommendUiState.postValue(state.modifyUiState(updated.copy(cartId = cartId)))
                                _cartUiState.postValue(_cartUiState.value?.addCart(cartId, updated))
                            }
                    }
                }

                else -> {
                    viewModelScope.launch(Dispatchers.IO) {
                        cartRepository.updateQuantity(cartId, updated.cartQuantity)
                            .onSuccess {
                                _recommendUiState.postValue(state.modifyUiState(updated))
                                increaseCartQuantity(cartId)
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
                viewModelScope.launch(Dispatchers.IO) {
                    cartRepository.updateQuantity(cartId, updated.cartQuantity)
                        .onSuccess {
                            _recommendUiState.postValue(state.modifyUiState(updated))
                            decreaseCartQuantity(cartId)
                        }
                }
            } else {
                viewModelScope.launch(Dispatchers.IO) {
                    cartRepository.deleteCart(cartId)
                        .onSuccess {
                            val result = updated.copy(cartId = null)
                            _recommendUiState.postValue(state.modifyUiState(result))
                            _cartUiState.postValue(_cartUiState.value?.deleteCart(cartId))
                        }
                }
            }
        }

    fun decreaseCartQuantity(cartId: Long) {
        withState(_cartUiState.value) { state ->
            val updatedState = state.decreaseCartQuantity(cartId)
            val updatedItem = updatedState.items.first { it.cartId == cartId }

            viewModelScope.launch(Dispatchers.IO) {
                cartRepository.updateQuantity(cartId, updatedItem.cart.quantity)
                    .onSuccess {
                        _cartUiState.postValue(updatedState)
                    }
            }
        }
    }

    fun increaseCartQuantity(cartId: Long) {
        withState(_cartUiState.value) { state ->
            val updatedState = state.increaseCartQuantity(cartId)
            val updatedItem = updatedState.findCart(cartId)

            viewModelScope.launch(Dispatchers.IO) {
                cartRepository.updateQuantity(cartId, updatedItem.cart.quantity)
                    .onSuccess { _cartUiState.postValue(updatedState) }
            }
        }
    }

    fun loadCarts() {
        setLoading(true)
        val nextPage = paging.getPageNo() - 1
        viewModelScope.launch(Dispatchers.IO) {
            cartRepository.loadSinglePage(
                nextPage,
                PAGE_SIZE,
            )
                .onSuccess { value ->
                    val pageState = paging.createPageState(!value.hasNextPage)
                    val newItems =
                        value
                            .carts
                            .map { CartState(it, _cartUiState.value?.allChecked ?: false) }

                    val currentItems = _cartUiState.value?.items ?: emptyList()
                    val combinedItems = currentItems + newItems

                    _cartUiState.postValue(CartUiState(items = combinedItems, pageState = pageState))
                    withContext(Dispatchers.Main) {
                        setLoading(false)
                    }
                }
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
        viewModelScope.launch(Dispatchers.IO) {
            cartRepository.deleteCart(productId)
                .onSuccess { refresh() }
        }
    }

    fun updateCheckedState(
        cartId: Long,
        isChecked: Boolean,
    ) {
        withState(_cartUiState.value) { state ->
            _cartUiState.postValue(state.modifyCheckedState(cartId, isChecked))
        }
    }

    fun changeAllStateChecked(isChecked: Boolean) {
        withState(_cartUiState.value) { state ->
            _cartUiState.postValue(state.setAllItemsChecked(isChecked))
        }
    }

    private fun refresh() {
        withState(_cartUiState.value) { state ->
            viewModelScope.launch(Dispatchers.IO) {
                cartRepository.loadSinglePage(paging.getPageNo() - 1, PAGE_SIZE)
                    .onSuccess { value ->
                        if (paging.resetToLastPageIfEmpty(value.carts)) {
                            refresh()
                        } else {
                            val pageState = paging.createPageState(!value.hasNextPage)
                            val carts = value.carts.map { CartState(it, state.allChecked) }

                            _cartUiState.postValue(
                                CartUiState(
                                    items = carts,
                                    pageState = pageState,
                                ),
                            )
                        }
                    }
            }
        }
    }

    fun onOrderButtonClicked() {
        val orders: List<ShoppingCart> = _cartUiState.value?.items?.filter { it.checked }?.map { it.cart } ?: emptyList()
        if (orders.isEmpty()) {
            requestNavigationToRecommendScreen()
        } else {
            requestNavigationToOrderScreen(orders)
        }
    }

    fun requestNavigationToRecommendScreen() {
        _event.setValue(CartUiEvent.NavigationToRecommendScreen)
    }

    fun requestNavigationToOrderScreen(orders: List<ShoppingCart>) {
        _event.setValue(CartUiEvent.NavigationToOrderScreen(orders))
    }

    private fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
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
