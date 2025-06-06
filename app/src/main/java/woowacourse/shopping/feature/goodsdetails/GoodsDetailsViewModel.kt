package woowacourse.shopping.feature.goodsdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.local.history.repository.HistoryRepository
import woowacourse.shopping.data.remote.cart.CartQuantity
import woowacourse.shopping.data.remote.cart.CartRepository
import woowacourse.shopping.data.remote.cart.CartRequest
import woowacourse.shopping.data.remote.product.ProductRepository
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.CartProduct.Companion.EMPTY_CART_PRODUCT
import woowacourse.shopping.domain.model.History
import woowacourse.shopping.feature.model.State
import woowacourse.shopping.util.Event
import woowacourse.shopping.util.MutableSingleLiveData
import woowacourse.shopping.util.SingleLiveData
import woowacourse.shopping.util.toDomain
import woowacourse.shopping.util.updateQuantity

class GoodsDetailsViewModel(
    private val cartRepository: CartRepository,
    private val historyRepository: HistoryRepository,
    private val productRepository: ProductRepository,
) : ViewModel() {
    private val _cartProduct = MutableLiveData<CartProduct>()
    val cartProduct: LiveData<CartProduct> get() = _cartProduct

    private val _lastViewed = MutableLiveData<History>()
    val lastViewed: LiveData<History> get() = _lastViewed

    private val _insertState = MutableLiveData<Event<State>>()
    val insertState: LiveData<Event<State>> get() = _insertState

    private val _navigateToLastViewedCart = MutableSingleLiveData<History>()
    val navigateToLastViewedCart: SingleLiveData<History> get() = _navigateToLastViewedCart

    private val _shouldShowLastViewed = MediatorLiveData<Boolean>()
    val shouldShowLastViewed: LiveData<Boolean> get() = _shouldShowLastViewed

    init {
        _shouldShowLastViewed.addSource(cartProduct) { updateLastViewedVisibility() }
    }

    suspend fun setInitialCart(id: Long) {
        loadProductDetails(productId = id)
        loadLastViewed()
    }

    suspend fun loadProductDetails(productId: Long) {
        val productContent = productRepository.requestProductDetails(productId = productId)
        viewModelScope.launch {
            val matchedCart = cartRepository.findCartByProductId(productContent.id)
            val cartProduct =
                CartProduct(
                    id = matchedCart?.id ?: 0,
                    product = productContent.toDomain(),
                    quantity = matchedCart?.quantity ?: 1,
                )

            _cartProduct.value = cartProduct
            insertToHistory(cartProduct)
        }
    }

    fun increaseQuantity() {
        val current = _cartProduct.value
        if (current != null) {
            val updated = current.updateQuantity(current.quantity + 1)
            _cartProduct.value = updated
        }
    }

    fun decreaseQuantity() {
        val current = _cartProduct.value
        if (current != null) {
            val updated = current.updateQuantity(current.quantity - 1)
            _cartProduct.value = updated
        }
    }

    fun commitCart() {
        if (cartProduct.value != null) {
            val currentCart = cartProduct.value ?: EMPTY_CART_PRODUCT
            val newQuantity = currentCart.quantity

            if (currentCart.quantity == 1) {
                val cartRequest =
                    CartRequest(
                        productId = currentCart.product.id,
                        quantity = newQuantity,
                    )

                viewModelScope.launch {
                    cartRepository
                        .addToCart(cartRequest)
                        .onSuccess { newCartId ->
                            val updatedCart = cartProduct.value?.copy(id = newCartId, quantity = newQuantity)

                            insertToHistory(cartProduct.value as CartProduct)
                            updateCart(updatedCart)
                            _insertState.value = Event(State.Success)
                        }.onFailure {
                            _insertState.value = Event(State.Failure)
                        }
                }
            } else {
                cartRepository.updateCart(
                    id = currentCart.id,
                    cartQuantity = CartQuantity(newQuantity),
                ) { result ->
                    result
                        .onSuccess {
                            val updatedCart = cartProduct.value?.copy(quantity = newQuantity)

                            insertToHistory(cartProduct.value as CartProduct)
                            updateCart(updatedCart)
                            _insertState.value = Event(State.Success)
                        }.onFailure { error ->
                            _insertState.value = Event(State.Failure)
                        }
                }
            }
        }
    }

    fun loadLastViewed() {
        historyRepository.findLatest { lastViewed ->
            _lastViewed.postValue(lastViewed)
        }
    }

    fun emitLastViewedCart() {
        val history = _lastViewed.value
        if (history != null) _navigateToLastViewedCart.postValue(history)
    }

    private fun updateLastViewedVisibility() {
        val history = lastViewed.value
        val currentCart = cartProduct.value
        _shouldShowLastViewed.value = history != null && history.name != currentCart?.product?.name
    }

    private fun updateCart(updatedCart: CartProduct?) {
        if (updatedCart != null) _cartProduct.value = updatedCart
    }

    private fun insertToHistory(cart: CartProduct) {
        historyRepository.insert(
            History(id = cart.product.id, name = cart.product.name, thumbnailUrl = cart.product.imageUrl),
        )
    }
}
