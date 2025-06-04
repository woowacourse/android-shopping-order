package woowacourse.shopping.feature.goodsdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.local.history.repository.HistoryRepository
import woowacourse.shopping.data.remote.cart.CartQuantity
import woowacourse.shopping.data.remote.cart.CartRepository
import woowacourse.shopping.data.remote.cart.CartRequest
import woowacourse.shopping.data.remote.product.ProductRepository
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.History
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.feature.model.State
import woowacourse.shopping.util.Event
import woowacourse.shopping.util.MutableSingleLiveData
import woowacourse.shopping.util.SingleLiveData
import woowacourse.shopping.util.updateQuantity

class GoodsDetailsViewModel(
    private val cartRepository: CartRepository,
    private val historyRepository: HistoryRepository,
    private val productRepository: ProductRepository,
) : ViewModel() {
    private val _cart = MutableLiveData<Cart>()
    val cart: LiveData<Cart> get() = _cart

    private val _lastViewed = MutableLiveData<History>()
    val lastViewed: LiveData<History> get() = _lastViewed

    private val _isLastViewedVisible = MutableLiveData<Boolean>()
    val isLastViewedVisible: LiveData<Boolean> get() = _isLastViewedVisible

    private val _insertState = MutableLiveData<Event<State>>()
    val insertState: LiveData<Event<State>> get() = _insertState

    private val _navigateToLastViewedCart = MutableSingleLiveData<History>()
    val navigateToLastViewedCart: SingleLiveData<History> get() = _navigateToLastViewedCart

    fun setInitialCart(id: Long) {
        loadProductDetails(productId = id)
        loadLastViewed()
    }

    fun loadProductDetails(productId: Long) {
        cartRepository.fetchAllCart(
            onSuccess = { response ->
                productRepository.requestProductDetails(
                    productId = productId,
                    onSuccess = { content ->
                        _cart.value =
                            Cart(
                                id =
                                    response.content
                                        .find {
                                            it.product.id == content.id
                                        }?.id ?: 0,
                                product =
                                    Product(
                                        id = content.id,
                                        name = content.name,
                                        price = content.price,
                                        imageUrl = content.imageUrl,
                                        category = content.category,
                                    ),
                                quantity =
                                    response.content.find { it.product.id == content.id }?.quantity
                                        ?: 1,
                            )
                        if (cart.value != null) insertToHistory(cart.value as Cart)
                    },
                    onError = {
                        _insertState.value = Event(State.Failure)
                    },
                )
            },
            onError = {},
        )
    }

    fun increaseQuantity() {
        val current = _cart.value
        if (current != null) {
            val updated = current.updateQuantity(current.quantity + 1)
            _cart.value = updated
        }
    }

    fun decreaseQuantity() {
        val current = _cart.value
        if (current != null) {
            val updated = current.updateQuantity(current.quantity - 1)
            _cart.value = updated
        }
    }

    fun commitCart() {
        if (cart.value != null) {
            val newQuantity = cart.value?.quantity ?: 0

            if (cart.value?.quantity == 1) {
                val cartRequest =
                    CartRequest(
                        productId = cart.value?.product?.id ?: 0,
                        quantity = newQuantity,
                    )

                cartRepository.addToCart(cartRequest) { result ->
                    result
                        .onSuccess { newCartId ->
                            val updatedCart = cart.value?.copy(id = newCartId, quantity = newQuantity)

                            insertToHistory(cart.value as Cart)
                            updateCart(updatedCart)
                            _insertState.value = Event(State.Success)
                        }.onFailure {
                            _insertState.value = Event(State.Failure)
                        }
                }
            } else {
                cartRepository.updateCart(
                    id = cart.value?.id ?: 0,
                    cartQuantity = CartQuantity(newQuantity),
                ) { result ->
                    result
                        .onSuccess {
                            val updatedCart = cart.value?.copy(quantity = newQuantity)

                            insertToHistory(cart.value as Cart)
                            updateCart(updatedCart)
                            _insertState.value = Event(State.Success)
                        }.onFailure { error ->
                            _insertState.value = Event(State.Failure)
                        }
                }
            }
        }
    }

    fun updateLastViewedVisibility() {
        val lastName = _lastViewed.value?.name
        val currentName = cart.value?.product?.name
        _isLastViewedVisible.postValue(lastName != null && currentName != null && lastName != currentName)
    }

    fun loadLastViewed() {
        historyRepository.findLatest { lastViewed ->
            _lastViewed.postValue(lastViewed)
            updateLastViewedVisibility()
        }
    }

    fun emitLastViewedCart() {
        val history = _lastViewed.value
        if (history != null) _navigateToLastViewedCart.postValue(history)
    }

    private fun updateCart(updatedCart: Cart?) {
        if (updatedCart != null) _cart.value = updatedCart
    }

    private fun insertToHistory(cart: Cart) {
        historyRepository.insert(
            History(id = cart.product.id, name = cart.product.name, thumbnailUrl = cart.product.imageUrl),
        )
    }
}
