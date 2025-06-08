package woowacourse.shopping.view.productDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    val price =
        MediatorLiveData<Int>().apply { product.value?.product?.price }
    private val _recentWatchingProduct: MutableLiveData<Product> = MutableLiveData()
    val recentWatchingProduct: LiveData<Product> get() = _recentWatchingProduct

    private val _recentProductBoxVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    val recentProductBoxVisible: LiveData<Boolean> get() = _recentProductBoxVisible

    init {
        price.apply {
            addSource(product) { updatePrice() }
            addSource(quantity) { updatePrice() }
        }
    }

    fun updatePrice() {
        price.value =
            product.value
                ?.product
                ?.price
                ?.times(quantity.value ?: 1)
    }

    fun updateProduct(
        productId: Long,
        shoppingCartQuantity: Int,
        shoppingCartId: Long?,
    ) {
        viewModelScope.launch {
            productsRepository
                .getProduct(productId)
                .onSuccess { product ->
                    if (product == null) {
                        _event.setValue(ProductDetailEvent.GET_PRODUCT_FAILURE)
                    } else {
                        _product.value =
                            ProductsItem.ProductItem(
                                product = product,
                                selectedQuantity = shoppingCartQuantity,
                                shoppingCartId = shoppingCartId,
                            )

                        updateRecentWatchingProduct()
                    }
                }.onFailure {
                    _event.setValue(ProductDetailEvent.GET_PRODUCT_FAILURE)
                }
        }
    }

    private fun updateRecentWatchingProduct() {
        viewModelScope.launch {
            productsRepository
                .getRecentWatchingProducts(1)
                .onSuccess { recentProducts: List<Product> ->
                    if (recentProducts.isEmpty()) return@launch updateRecentWatching()
                    val isLastWatchingProduct =
                        recentProducts.first() == this@ProductDetailViewModel.product.value?.product
                    if (isLastWatchingProduct) {
                        _recentProductBoxVisible.value = false
                        return@launch
                    }
                    _recentWatchingProduct.value = recentProducts[0]
                    _recentProductBoxVisible.value = true
                    updateRecentWatching()
                }.onFailure {
                    _event.setValue(ProductDetailEvent.GET_RECENT_WATCHING_FAILURE)
                }
        }
    }

    private fun updateRecentWatching() {
        viewModelScope.launch {
            productsRepository
                .updateRecentWatchingProduct(
                    product.value?.product ?: return@launch,
                ).onFailure {
                    _event.setValue(ProductDetailEvent.ADD_RECENT_WATCHING_FAILURE)
                }
        }
    }

    fun addToShoppingCart() {
        val product = requireNotNull(product.value) { "product.value가 null입니다." }
        val totalQuantity = quantity.value?.plus(product.selectedQuantity) ?: 0
        if (product.shoppingCartId == null) {
            shoppingCartRepository.add(
                product.product,
                totalQuantity,
            ) { result ->
                result
                    .onSuccess {
                        _event.postValue(ProductDetailEvent.ADD_SHOPPING_CART_SUCCESS)
                    }.onFailure {
                        _event.postValue(ProductDetailEvent.ADD_SHOPPING_CART_FAILURE)
                    }
            }
            return
        }
        shoppingCartRepository.increaseQuantity(
            product.shoppingCartId,
            totalQuantity,
        ) { result ->
            result
                .onSuccess {
                    _event.postValue(ProductDetailEvent.ADD_SHOPPING_CART_SUCCESS)
                }.onFailure {
                    _event.postValue(ProductDetailEvent.ADD_SHOPPING_CART_FAILURE)
                }
        }
    }

    fun plusQuantity() {
        _quantity.value = (_quantity.value ?: 0) + 1
    }

    fun minusQuantity() {
        _quantity.value = (_quantity.value)?.minus(1)?.coerceAtLeast(1)
    }
}
