package woowacourse.shopping.feature.goodsdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import woowacourse.shopping.data.local.history.repository.HistoryRepository
import woowacourse.shopping.data.remote.cart.CartQuantity
import woowacourse.shopping.data.remote.cart.CartRepository
import woowacourse.shopping.data.remote.cart.CartRequest
import woowacourse.shopping.data.remote.product.ProductRepository
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.util.MutableSingleLiveData
import woowacourse.shopping.util.SingleLiveData

class GoodsDetailsViewModel(
    private val cartRepository: CartRepository,
    private val historyRepository: HistoryRepository,
    private val productRepository: ProductRepository,
) : ViewModel() {
    private val _product = MutableLiveData<Product>()
    val product: LiveData<Product> get() = _product

    private val _quantity = MutableLiveData<Int>()
    val quantity: LiveData<Int> get() = _quantity

    private val _lastViewed = MutableLiveData<Product>()
    val lastViewed: LiveData<Product> get() = _lastViewed

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
            productRepository
                .requestProductDetails(productId)
                .onSuccess { productId ->
                    val product =
                        Product(
                            productId?.id?.toInt() ?: 0,
                            productId?.name ?: "",
                            productId?.price ?: 0,
                            productId?.imageUrl ?: "",
                            "",
                        )
                    _product.value = product
                }.onFailure {
                    _isFail.setValue(Unit)
                }
        }
    }

    fun increaseQuantity() {
        val current = _quantity.value
        if (current != null) {
            val updated = current + 1
            _quantity.value = updated
        }
    }

    fun decreaseQuantity() {
        val current = _quantity.value
        if (current != null) {
            val updated = (current - 1).coerceAtLeast(1)
            _quantity.value = updated
        }
    }

    fun commitCart() {
        val product = _product.value ?: return
        val productId = product.id
        val quantity = _quantity.value ?: return
        viewModelScope.launch {
            cartRepository
                .fetchAllCart()
                .onSuccess {
                    it?.let {
                        val cartId =
                            it.content
                                .find {
                                    // 장바구니에 동일한 상품이 저장되었는가 ?
                                    it.product.id == productId.toLong()
                                }?.id

                        if (cartId == null) {
                            // 저장 되어 있지 않음
                            cartRepository
                                .addToCart(CartRequest(productId, quantity))
                                .onSuccess { response ->
                                    insertToHistory(Cart(response, product, quantity))
                                    _isSuccess.setValue(Unit)
                                }.onFailure {
                                    _isFail.setValue(Unit)
                                }
                        } else {
                            // 저장됨
                            cartRepository.updateCart(cartId, CartQuantity(quantity))
                        }
                    }
                }.onFailure { }
        }
    }

    fun updateLastViewedVisibility() {
        val lastName = _lastViewed.value?.name
        val currentName = _product.value?.name
        _isLastViewedVisible.postValue(lastName != null && currentName != null && lastName != currentName)
    }

    fun loadLastViewed() {
        viewModelScope.launch(Dispatchers.IO) {
            val lastViewed = historyRepository.findLatest()
            withContext(Dispatchers.Main) {
                lastViewed?.let { _lastViewed.value }
            }
            updateLastViewedVisibility()
        }
    }

    private fun insertToHistory(cart: Cart) {
        viewModelScope.launch(Dispatchers.IO) {
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
}
