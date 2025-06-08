package woowacourse.shopping.feature.goodsdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.local.history.repository.HistoryRepository
import woowacourse.shopping.data.remote.cart.CartQuantity
import woowacourse.shopping.data.remote.cart.CartRepository
import woowacourse.shopping.data.remote.cart.CartRequest
import woowacourse.shopping.data.remote.product.ProductRepository
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.Product
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

    private val _lastViewed = MutableLiveData<Cart>()
    val lastViewed: LiveData<Cart> get() = _lastViewed

    private val _isLastViewedVisible = MutableLiveData<Boolean>()
    val isLastViewedVisible: LiveData<Boolean> get() = _isLastViewedVisible

    private val _isSuccess = MutableSingleLiveData<Unit>()
    val isSuccess: SingleLiveData<Unit> get() = _isSuccess

    private val _isFail = MutableSingleLiveData<Unit>()
    val isFail: SingleLiveData<Unit> get() = _isFail

    private val _navigateToLastViewedCart = MutableSingleLiveData<Cart>()
    val navigateToLastViewedCart: SingleLiveData<Cart> get() = _navigateToLastViewedCart

    fun setInitialCart(id: Long) {
        loadProductDetails(productId = id)
        loadLastViewed()
    }

    fun loadProductDetails(productId: Long) {
        viewModelScope.launch {
            cartRepository
                .fetchAllCart()
                .onSuccess { cartResponse ->
                    productRepository
                        .requestProductDetails(
                            productId = productId,
                        ).onSuccess { productResponse ->
                            if (cartResponse != null && productResponse != null) {
                                _cart.value =
                                    Cart(
                                        id =
                                            cartResponse.content
                                                .find {
                                                    it.product.id == productResponse.id
                                                }?.id ?: 0,
                                        product =
                                            Product(
                                                id = productResponse.id.toInt(),
                                                name = productResponse.name,
                                                price = productResponse.price,
                                                imageUrl = productResponse.imageUrl,
                                                category = productResponse.category,
                                            ),
                                        quantity =
                                            cartResponse.content.find { it.product.id == productResponse.id }?.quantity
                                                ?: 1,
                                    )
                                if (cart.value != null) insertToHistory(cart.value as Cart)
                            }
                        }.onFailure {
                            _isFail.setValue(Unit)
                        }
                }.onFailure {
                    _isFail.setValue(Unit)
                }
        }
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
        viewModelScope.launch {
            val newQuantity = cart.value?.quantity ?: 0
            if (cart.value != null) {
                if (cart.value?.quantity == 1) {
                    val cartRequest =
                        CartRequest(
                            productId = cart.value?.product?.id ?: 0,
                            quantity = newQuantity,
                        )

                    cartRepository
                        .addToCart(cartRequest)
                        .onSuccess { response ->
                            val updatedCart =
                                cart.value?.copy(id = response, quantity = newQuantity)

                            insertToHistory(cart.value as Cart)
                            updateCart(updatedCart)
                            _isSuccess.setValue(Unit)
                        }.onFailure {
                            _isFail.setValue(Unit)
                        }
                }
            } else {
                cartRepository
                    .updateCart(
                        id = cart.value?.id ?: 0,
                        cartQuantity = CartQuantity(newQuantity),
                    ).onSuccess {
                        val updatedCart = cart.value?.copy(quantity = newQuantity)

                        insertToHistory(cart.value as Cart)
                        updateCart(updatedCart)
                        _isSuccess.setValue(Unit)
                    }.onFailure { error ->
                        _isFail.setValue(Unit)
                    }
            }
        }
    }

    fun updateLastViewedVisibility() {
        val lastName = _lastViewed.value?.product?.name
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
            Cart(
                cart.id,
                Product(
                    cart.product.id,
                    cart.product.name,
                    cart.product.price,
                    cart.product.imageUrl,
                    "",
                ),
                cart.quantity,
            ),
        )
    }
}
