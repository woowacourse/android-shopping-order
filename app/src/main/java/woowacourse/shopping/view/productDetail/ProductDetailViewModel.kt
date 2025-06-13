package woowacourse.shopping.view.productDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private val _loading: MutableLiveData<Boolean> = MutableLiveData(true)
    val loading: LiveData<Boolean> = _loading

    init {
        loadCartItems()
        loadLatestViewedProduct()
    }

    private fun loadCartItems() {
        viewModelScope.launch {
            runCatching {
                cartRepository.loadCart()
            }.onSuccess { cartItems: List<CartItem> ->
                this@ProductDetailViewModel.cartItems = cartItems
            }.onFailure {
                _event.postValue(ProductDetailEvent.LOAD_SHOPPING_CART_FAILURE)
            }
        }
    }

    private fun loadLatestViewedProduct() {
        viewModelScope.launch {
            val product =
                runCatching {
                    productsRepository.loadLatestViewedProduct()
                }.getOrNull()

            _latestViewedProduct.postValue(product)
        }
    }

    fun loadProduct(productId: Long) {
        viewModelScope.launch {
            runCatching {
                productsRepository.getProductById(productId)
            }.onSuccess { product ->
                _product.postValue(product)
                if (product != null) addViewedProduct(product)
                _loading.postValue(false)
            }.onFailure {
                _event.postValue(ProductDetailEvent.LOAD_PRODUCT_FAILURE)
            }
        }
    }

    private fun addViewedProduct(product: Product) {
        viewModelScope.launch {
            runCatching {
                productsRepository.addViewedProduct(product)
            }.onFailure {
                _event.postValue(ProductDetailEvent.RECORD_RECENT_PRODUCT_FAILURE)
            }
        }
    }

    fun addToShoppingCart() {
        val productId: Long = product.value?.id ?: return
        val currentQuantity = quantity.value ?: QUANTITY_MIN
        val existingItem = cartItems.find { it.productId == productId }

        viewModelScope.launch {
            runCatching {
                if (existingItem == null) {
                    cartRepository.addCartItem(productId, currentQuantity)
                } else {
                    cartRepository.updateCartItemQuantity(existingItem.id, currentQuantity)
                }
            }.onSuccess {
                _event.postValue(ProductDetailEvent.ADD_SHOPPING_CART_SUCCESS)
            }.onFailure {
                _event.postValue(ProductDetailEvent.ADD_SHOPPING_CART_FAILURE)
            }
        }
    }

    fun plusProductQuantity() {
        _quantity.value = (quantity.value ?: QUANTITY_MIN) + 1
    }

    fun minusProductQuantity() {
        _quantity.value = ((quantity.value ?: (QUANTITY_MIN - 1))).coerceAtLeast(QUANTITY_MIN)
    }

    companion object {
        private const val QUANTITY_MIN = 1
    }
}
