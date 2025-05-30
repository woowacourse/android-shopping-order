package woowacourse.shopping.view.cart.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.domain.Quantity
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.view.cart.CartUiEvent
import woowacourse.shopping.view.cart.state.CartState
import woowacourse.shopping.view.cart.state.CartUiState
import woowacourse.shopping.view.cart.vm.Paging.Companion.INITIAL_PAGE_NO
import woowacourse.shopping.view.cart.vm.Paging.Companion.PAGE_SIZE
import woowacourse.shopping.view.core.common.withState
import woowacourse.shopping.view.core.event.MutableSingleLiveData
import woowacourse.shopping.view.core.event.SingleLiveData
import woowacourse.shopping.view.main.state.ProductState

class CartViewModel(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
) : ViewModel() {
    private val paging = Paging(initialPage = INITIAL_PAGE_NO, pageSize = PAGE_SIZE)

    private val _cartUiState = MutableLiveData<CartUiState>()
    val cartUiState: LiveData<CartUiState> get() = _cartUiState

    private val _recommendUiState = MutableLiveData<List<ProductState>>()
    val recommendUiState: LiveData<List<ProductState>> get() = _recommendUiState

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
        withState(_cartUiState.value) { state ->
            productRepository.loadSinglePage(lastSeenCategory, null, null) { products ->
                products.fold(
                    onSuccess = { result ->

                        val recommentProduct =
                            result
                                .products
                                .asSequence()
                                .filterNot { it.id !in state.cartIds }
                                .shuffled()
                                .take(RECOMMEND_SIZE)
                                .map { ProductState(item = it, cartQuantity = Quantity(0)) }
                                .toList()

                        _recommendUiState.value = recommentProduct
                    },
                    onFailure = {},
                )
            }
        }
    }

//    fun increaseCartQuantity(productId: Long) =
//        withState(_uiState.value) { state ->
//            val updated = state.increaseCartQuantity(productId)
//
//            when (val cartId = updated.cartId) {
//                null -> {
//                    cartRepository.addCart(Cart(updated.cartQuantity, productId)) {
//                        it.fold(
//                            onSuccess = { value ->
//                                _uiState.value = state.modifyUiState(updated.copy(value?.toLong()))
//                            },
//                            onFailure = { handleError(TAG_INCREASE, it) },
//                        )
//                    }
//                }
//
//                else -> {
//                    cartRepository.updateQuantity(cartId, updated.cartQuantity) {
//                        it.fold(
//                            onSuccess = {
//                                _uiState.value = state.modifyUiState(updated)
//                            },
//                            onFailure = { handleError(TAG_INCREASE, it) },
//                        )
//                    }
//                }
//            }
//        }

    fun decreaseCartQuantity(productId: Long) {
        withState(_cartUiState.value) { state ->
            val updatedState = state.decreaseCartQuantity(productId)
            val updatedItem = updatedState.items.first { it.cartId == productId }

            cartRepository.updateQuantity(productId, updatedItem.cart.quantity) { result ->
                result.fold(
                    onSuccess = {
                        _cartUiState.value = updatedState
                    },
                    onFailure = {},
                )
            }
        }
    }

    fun increaseCartQuantity(productId: Long) {
        withState(_cartUiState.value) { state ->
            val updatedState = state.increaseCartQuantity(productId)
            val updatedItem = updatedState.items.first { it.cartId == productId }

            cartRepository.updateQuantity(productId, updatedItem.cart.quantity) { result ->
                result.fold(
                    onSuccess = {
                        _cartUiState.value = updatedState
                    },
                    onFailure = {},
                )
            }
        }
    }

    fun loadCarts() {
        setLoading(true)
        val nextPage = paging.getPageNo() - 1
        cartRepository.loadSinglePage(
            nextPage,
            PAGE_SIZE,
        ) { result ->

            result.fold(
                onSuccess = { value ->
                    val pageState = paging.createPageState(!value.hasNextPage)
                    val newItems =
                        value
                            .carts
                            .map { CartState(it, _cartUiState.value?.allChecked ?: false) }

                    val currentItems = _cartUiState.value?.items ?: emptyList()
                    val combinedItems = currentItems + newItems

                    _cartUiState.value = CartUiState(items = combinedItems, pageState = pageState)
                },
                onFailure = {},
            )
            setLoading(false)
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
                result.fold(
                    onSuccess = { value ->
                        if (paging.resetToLastPageIfEmpty(value.carts)) {
                            refresh()
                            return@loadSinglePage
                        }

                        val pageState = paging.createPageState(!value.hasNextPage)
                        val carts = value.carts.map { CartState(it, state.allChecked) }

                        _cartUiState.postValue(CartUiState(items = carts, pageState = pageState))
                    },
                    onFailure = {},
                )
            }
        }
    }

    fun onChangeScreen() {
        _event.setValue(CartUiEvent.ChangeScreen)
    }

    private fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

    companion object {
        private const val RECOMMEND_SIZE = 10
    }
}
