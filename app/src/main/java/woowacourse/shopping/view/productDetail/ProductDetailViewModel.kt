package woowacourse.shopping.view.productDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import woowacourse.shopping.data.product.repository.DefaultProductsRepository
import woowacourse.shopping.data.product.repository.ProductsRepository
import woowacourse.shopping.data.shoppingCart.repository.DefaultShoppingCartRepository
import woowacourse.shopping.data.shoppingCart.repository.ShoppingCartRepository
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.view.common.MutableSingleLiveData
import woowacourse.shopping.view.common.SingleLiveData
import woowacourse.shopping.view.product.ProductsItem

class ProductDetailViewModel(
    productId: Long,
    shoppingCartId: Long?,
    shoppingCartQuantity: Int,
    private val shoppingCartRepository: ShoppingCartRepository = DefaultShoppingCartRepository.get(),
    private val productsRepository: ProductsRepository = DefaultProductsRepository.get(),
) : ViewModel() {
    private val _product: MutableLiveData<ProductsItem.ProductItem> = MutableLiveData()
    val product: LiveData<ProductsItem.ProductItem> get() = _product

    private val _event: MutableSingleLiveData<ProductDetailEvent> =
        MutableSingleLiveData()
    val event: SingleLiveData<ProductDetailEvent> get() = _event

    private val _quantity: MutableLiveData<Int> = MutableLiveData(1)
    val quantity: LiveData<Int> get() = _quantity

    private val _price: MutableLiveData<Int> = MutableLiveData()
    val price: LiveData<Int> get() = _price

    private val _recentWatchingProduct: MutableLiveData<Product> = MutableLiveData()
    val recentWatchingProduct: LiveData<Product> get() = _recentWatchingProduct

    private val _recentProductBoxVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    val recentProductBoxVisible: LiveData<Boolean> get() = _recentProductBoxVisible

    private val handler =
        CoroutineExceptionHandler { _, exception ->
            _event.postValue(ProductDetailEvent.GET_PRODUCT_FAILURE)
        }

    init {
        viewModelScope.launch(handler) {
            val product = productsRepository.getProduct(productId).getOrThrow()
            if (product == null) {
                _event.setValue(ProductDetailEvent.GET_PRODUCT_FAILURE)
            } else {
                _product.value =
                    ProductsItem.ProductItem(
                        product = product,
                        selectedQuantity = shoppingCartQuantity,
                        shoppingCartId = shoppingCartId,
                    )
                _price.value = product.price
                updateRecentWatchingProduct()
            }
        }
    }

    private fun updateRecentWatchingProduct() {
        viewModelScope.launch {
            val recentProducts = productsRepository.getLatestRecentWatchingProduct().getOrThrow()
            val isLastWatchingProduct = recentProducts == product.value?.product
            if (isLastWatchingProduct) {
                _recentProductBoxVisible.postValue(false)
                return@launch
            }
            _recentWatchingProduct.postValue(recentProducts)
            _recentProductBoxVisible.postValue(true)
            updateRecentWatching()
        }
    }

    private fun updateRecentWatching() {
        val productDomain = product.value?.product
        if (productDomain == null) return
        viewModelScope.launch {
            productsRepository.updateRecentWatchingProduct(productDomain)
        }
    }

    fun addToShoppingCart() {
        val product = requireNotNull(product.value) { "product.value가 null입니다." }
        val totalQuantity = quantity.value?.plus(product.selectedQuantity) ?: 0
        if (product.shoppingCartId == null) {
            viewModelScope.launch(handler) {
                shoppingCartRepository.add(
                    product.product,
                    totalQuantity,
                ).getOrThrow()
                _event.postValue(ProductDetailEvent.ADD_SHOPPING_CART_SUCCESS)
            }
            return
        }
        viewModelScope.launch(handler) {
            shoppingCartRepository.updateQuantity(
                product.shoppingCartId,
                totalQuantity,
            ).getOrThrow()
            _event.postValue(ProductDetailEvent.ADD_SHOPPING_CART_SUCCESS)
        }
    }

    fun plusQuantity() {
        _quantity.value = (_quantity.value ?: 0) + 1
        _price.value = quantity.value?.times(product.value?.product?.price ?: 0)
    }

    fun minusQuantity() {
        _quantity.value = (_quantity.value)?.minus(1)?.coerceAtLeast(1)
        _price.value = quantity.value?.times(product.value?.product?.price ?: 0)
    }

    companion object {
        fun factory(
            productId: Long,
            shoppingCartId: Long?,
            shoppingCartQuantity: Int,
        ): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    ProductDetailViewModel(
                        productId,
                        shoppingCartId,
                        shoppingCartQuantity,
                    )
                }
            }
    }
}
