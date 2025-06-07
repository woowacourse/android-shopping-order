package woowacourse.shopping.view.productDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import woowacourse.shopping.data.cart.repository.CartRepository
import woowacourse.shopping.data.cart.repository.DefaultCartRepository
import woowacourse.shopping.data.product.repository.DefaultProductsRepository
import woowacourse.shopping.data.product.repository.ProductsRepository
import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.view.MutableSingleLiveData
import woowacourse.shopping.view.SingleLiveData

class ProductDetailViewModel(
    private val productsRepository: ProductsRepository = DefaultProductsRepository(),
    private val cartRepository: CartRepository = DefaultCartRepository(),
) : ViewModel() {
    private val _event: MutableSingleLiveData<ProductDetailEvent> = MutableSingleLiveData()
    val event: SingleLiveData<ProductDetailEvent> get() = _event

    private val _product: MutableLiveData<Product> = MutableLiveData()
    val product: LiveData<Product> get() = _product

    private val _quantity: MutableLiveData<Int> = MutableLiveData(QUANTITY_MIN)
    val quantity: LiveData<Int> get() = _quantity

    private var cartItems: List<CartItem> = emptyList()

    private val _latestViewedProduct: MutableLiveData<Product?> = MutableLiveData()
    val latestViewedProduct: LiveData<Product?> get() = _latestViewedProduct

    val loading: MutableLiveData<Boolean> = MutableLiveData(true)

    init {
        loadCartItems()
        loadLatestViewedProduct()
    }

    private fun loadCartItems() {
        viewModelScope.launch {
            cartRepository
                .loadCart()
                .onSuccess { cartItems: List<CartItem> ->
                    this@ProductDetailViewModel.cartItems = cartItems
                }.onFailure {
                    _event.value = ProductDetailEvent.LOAD_SHOPPING_CART_FAILURE
                }
        }
    }

    private fun loadLatestViewedProduct() {
        viewModelScope.launch {
            _latestViewedProduct.value = productsRepository.loadLatestViewedProduct().getOrNull()
        }
    }

    fun loadProduct(productId: Long) {
        viewModelScope.launch {
            productsRepository
                .getProductById(productId)
                .onSuccess { product ->
                    _product.value = product

                    if (product != null) addViewedProduct(product)
                    delay(LOADING_TIME)

                    loading.value = false
                }.onFailure {
                    _event.value = ProductDetailEvent.LOAD_PRODUCT_FAILURE
                }
        }
    }

    private fun addViewedProduct(product: Product) {
        viewModelScope.launch {
            productsRepository.addViewedProduct(product).onFailure {
                _event.value = ProductDetailEvent.RECORD_RECENT_PRODUCT_FAILURE
            }
        }
    }

    fun addToShoppingCart() {
        viewModelScope.launch {
            val productId: Long = product.value?.id ?: error("")
            val cartItem: CartItem? =
                cartItems
                    .find { cartItem: CartItem ->
                        cartItem.productId == productId
                    }

            if (cartItem == null) {
                cartRepository
                    .addCartItem(productId, quantity.value ?: 1)
                    .onSuccess {
                        _event.value = ProductDetailEvent.ADD_SHOPPING_CART_SUCCESS
                    }.onFailure {
                        _event.value = ProductDetailEvent.ADD_SHOPPING_CART_FAILURE
                    }
            } else {
                cartRepository
                    .updateCartItemQuantity(cartItem.id, quantity.value ?: 1)
                    .onSuccess {
                        _event.value = ProductDetailEvent.ADD_SHOPPING_CART_SUCCESS
                    }.onFailure {
                        _event.value = ProductDetailEvent.ADD_SHOPPING_CART_FAILURE
                    }
            }
        }
    }

    fun plusProductQuantity() {
        _quantity.value = quantity.value?.plus(1) ?: 2
    }

    fun minusProductQuantity() {
        _quantity.value = quantity.value?.minus(1)?.coerceAtLeast(1) ?: QUANTITY_MIN
    }

    companion object {
        private const val QUANTITY_MIN = 1
        private const val LOADING_TIME = 500L
    }
}
