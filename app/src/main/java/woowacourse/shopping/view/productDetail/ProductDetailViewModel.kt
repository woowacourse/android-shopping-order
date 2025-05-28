package woowacourse.shopping.view.productDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.product.repository.DefaultProductsRepository
import woowacourse.shopping.data.product.repository.ProductsRepository
import woowacourse.shopping.data.shoppingCart.repository.CartRepository
import woowacourse.shopping.data.shoppingCart.repository.DefaultCartRepository
import woowacourse.shopping.domain.product.CartItem
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

    init {
        loadCartItems()
        loadLatestViewedProduct()
    }

    private fun loadCartItems() {
        cartRepository.loadCart { result ->
            result
                .onSuccess { cartItems: List<CartItem> ->
                    this.cartItems = cartItems
                }.onFailure {
                    _event.postValue(ProductDetailEvent.LOAD_SHOPPING_CART_FAILURE)
                }
        }
    }

    private fun loadLatestViewedProduct() {
        productsRepository.loadLatestViewedProduct { result ->
            _latestViewedProduct.postValue(result.getOrNull())
        }
    }

    fun loadProduct(productId: Long) {
        productsRepository.getProductById(productId) { result ->
            result
                .onSuccess { product ->
                    _product.postValue(product)
                    if (product != null) addViewedProduct(product)
                }.onFailure {
                    _event.postValue(ProductDetailEvent.LOAD_PRODUCT_FAILURE)
                }
        }
    }

    private fun addViewedProduct(product: Product) {
        productsRepository.addViewedProduct(product) { result ->
            result.onFailure {
                _event.postValue(ProductDetailEvent.RECORD_RECENT_PRODUCT_FAILURE)
            }
        }
    }

    fun addToShoppingCart() {
        val productId: Long = product.value?.id ?: error("")
        val cartItem: CartItem? =
            cartItems
                .find { cartItem: CartItem ->
                    cartItem.productId == productId
                }

        if (cartItem == null) {
            cartRepository.addCartItem(
                productId = productId,
                quantity = quantity.value ?: 1,
            ) { result ->
                result
                    .onSuccess {
                        _event.postValue(ProductDetailEvent.ADD_SHOPPING_CART_SUCCESS)
                    }.onFailure {
                        _event.postValue(ProductDetailEvent.ADD_SHOPPING_CART_FAILURE)
                    }
            }
        } else {
            cartRepository.updateCartItemQuantity(
                cartItemId = cartItem.id,
                quantity = quantity.value ?: 1,
            ) { result ->
                result
                    .onSuccess {
                        _event.postValue(ProductDetailEvent.ADD_SHOPPING_CART_SUCCESS)
                    }.onFailure {
                        _event.postValue(ProductDetailEvent.ADD_SHOPPING_CART_FAILURE)
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

    //    private val _product: MutableLiveData<Product> = MutableLiveData()
//    val product: LiveData<Product> get() = _product
//
//    val imageUrl: LiveData<String?> = _product.map { it.imageUrl }
//
//    private val _quantity: MutableLiveData<Int> = MutableLiveData(QUANTITY_MIN)
//    val quantity: LiveData<Int> get() = _quantity
//
//    private val _latestViewedProduct: MutableLiveData<Product?> = MutableLiveData()
//    val latestViewedProduct: LiveData<Product?> get() = _latestViewedProduct
//
//    private val _event: MutableSingleLiveData<ProductDetailEvent> = MutableSingleLiveData()
//    val event: SingleLiveData<ProductDetailEvent> get() = _event
//
//    init {
//        loadLatestViewedProduct()
//    }
//
//    fun updateProduct(id: Long) {
//        productsRepository.getProductById(id) { result: Result<Product?> ->
//            result
//                .onSuccess { product: Product? ->
//                    _product.postValue(product)
//                    if (product != null) {
//                        recordRecentProduct(product)
//                    }
//                }.onFailure {
//                    _event.postValue(ProductDetailEvent.LOAD_PRODUCT_FAILURE)
//                }
//        }
//    }
//
//    fun loadLatestViewedProduct() {
//        productsRepository.loadLatestViewedProduct { result: Result<Product?> ->
//            _latestViewedProduct.postValue(result.getOrNull())
//        }
//    }
//
//    fun addToShoppingCart() {
//        val product: Product =
//            product.value ?: run {
//                _event.setValue(ProductDetailEvent.ADD_SHOPPING_CART_FAILURE)
//                return
//            }
//
//        cartRepository.addCartItem(
//            product.id,
//            quantity.value ?: 1,
//        ) { result: Result<Unit> ->
//            result
//                .onSuccess {
//                    _event.postValue(ProductDetailEvent.ADD_SHOPPING_CART_SUCCESS)
//                }.onFailure {
//                    _event.postValue(ProductDetailEvent.ADD_SHOPPING_CART_FAILURE)
//                }
//        }
//    }
//
//    private fun recordRecentProduct(product: Product) {
//        productsRepository.addViewedProduct(product) { result ->
//            result
//                .onFailure {
//                    _event.postValue(ProductDetailEvent.RECORD_RECENT_PRODUCT_FAILURE)
//                }
//        }
//    }
//
//    fun plusProductQuantity() {
//        _quantity.value = quantity.value?.plus(1) ?: 2
//    }
//
//    fun minusProductQuantity() {
//        _quantity.value = quantity.value?.minus(1)?.coerceAtLeast(QUANTITY_MIN) ?: QUANTITY_MIN
//    }
//
    companion object {
        private const val QUANTITY_MIN = 1
    }
}
