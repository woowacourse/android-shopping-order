package woowacourse.shopping.feature.goodsdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.local.cart.repository.LocalCartRepository
import woowacourse.shopping.data.local.history.repository.HistoryRepository
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.util.MutableSingleLiveData
import woowacourse.shopping.util.SingleLiveData
import woowacourse.shopping.util.updateQuantity

class GoodsDetailsViewModel(
    private val cartRepository: LocalCartRepository,
    private val historyRepository: HistoryRepository,
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

    fun setInitialCart(cart: Cart) {
        _cart.value = cart
        insertToHistory(cart)
        loadLastViewed()
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
        runCatching {
            _cart.value?.let {
                cartRepository.insertAll(it)
            }
        }.onSuccess {
            _isSuccess.setValue(Unit)
        }.onFailure {
            _isFail.setValue(Unit)
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

    private fun insertToHistory(cart: Cart) {
        historyRepository.insert(Cart(cart.id, Product(cart.product.id, cart.product.name, cart.product.price, cart.product.imageUrl, ""), cart.quantity))
    }
}
