package woowacourse.shopping.view.productDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import woowacourse.shopping.data.product.repository.DefaultProductsRepository
import woowacourse.shopping.data.product.repository.ProductsRepository
import woowacourse.shopping.data.shoppingCart.repository.DefaultShoppingCartRepository
import woowacourse.shopping.data.shoppingCart.repository.ShoppingCartRepository
import woowacourse.shopping.domain.product.CartItem
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.view.MutableSingleLiveData
import woowacourse.shopping.view.SingleLiveData

class ProductDetailViewModel(
    private val productsRepository: ProductsRepository = DefaultProductsRepository(),
    private val shoppingCartRepository: ShoppingCartRepository = DefaultShoppingCartRepository(),
) : ViewModel() {
    private val _product: MutableLiveData<Product> = MutableLiveData()
    val product: LiveData<Product> get() = _product

    val imageUrl: LiveData<String?> = _product.map { it.imageUrl }

    private val _quantity: MutableLiveData<Int> = MutableLiveData(QUANTITY_MIN)
    val quantity: LiveData<Int> get() = _quantity

    private val _latestViewedProduct: MutableLiveData<Product?> = MutableLiveData()
    val latestViewedProduct: LiveData<Product?> get() = _latestViewedProduct

    private val _event: MutableSingleLiveData<ProductDetailEvent> = MutableSingleLiveData()
    val event: SingleLiveData<ProductDetailEvent> get() = _event

    init {
        loadLatestViewedProduct()
    }

    fun updateProduct(id: Long) {
        productsRepository.loadProductById(id) { result: Result<Product?> ->
            result
                .onSuccess { product: Product? ->
                    _product.postValue(product)
                    if (product != null) {
                        recordRecentProduct(product)
                    }
                }.onFailure {
                    _event.postValue(ProductDetailEvent.LOAD_PRODUCT_FAILURE)
                }
        }
    }

    fun loadLatestViewedProduct() {
        productsRepository.loadLatestViewedProduct { result: Result<Product?> ->
            _latestViewedProduct.postValue(result.getOrNull())
        }
    }

    fun addToShoppingCart() {
        val product: Product =
            product.value ?: run {
                _event.setValue(ProductDetailEvent.ADD_SHOPPING_CART_FAILURE)
                return
            }
        val cartItem = CartItem(product, quantity.value ?: 1)

        shoppingCartRepository.upsert(cartItem) { result: Result<Unit> ->
            result
                .onSuccess {
                    _event.postValue(ProductDetailEvent.ADD_SHOPPING_CART_SUCCESS)
                }.onFailure {
                    _event.postValue(ProductDetailEvent.ADD_SHOPPING_CART_FAILURE)
                }
        }
    }

    private fun recordRecentProduct(product: Product) {
        productsRepository.recordViewedProduct(product) { result ->
            result
                .onFailure {
                    _event.postValue(ProductDetailEvent.RECORD_RECENT_PRODUCT_FAILURE)
                }
        }
    }

    fun plusProductQuantity() {
        _quantity.value = quantity.value?.plus(1) ?: 2
    }

    fun minusProductQuantity() {
        _quantity.value = quantity.value?.minus(1)?.coerceAtLeast(QUANTITY_MIN) ?: QUANTITY_MIN
    }

    companion object {
        private const val QUANTITY_MIN = 1
    }
}
