package woowacourse.shopping.ui.detail.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import woowacourse.shopping.data.cart.CartRepository
import woowacourse.shopping.data.product.ProductRepository
import woowacourse.shopping.data.recentproduct.RecentProductRepository
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.ProductWithQuantity
import woowacourse.shopping.ui.listener.CountButtonClickListener
import woowacourse.shopping.ui.utils.MutableSingleLiveData
import woowacourse.shopping.ui.utils.SingleLiveData

class ProductDetailViewModel(
    private val productId: Long,
    private val lastSeenProductState: Boolean,
    private val productRepository: ProductRepository,
    private val recentProductRepository: RecentProductRepository,
    private val cartRepository: CartRepository,
) : ViewModel(), CountButtonClickListener {
    private val _error: MutableLiveData<Boolean> = MutableLiveData(false)
    val error: LiveData<Boolean> get() = _error

    private val _errorMsg = MutableSingleLiveData<String>()
    val errorMsg: SingleLiveData<String> get() = _errorMsg

    private val _productWithQuantity: MutableLiveData<ProductWithQuantity> = MutableLiveData()
    val productWithQuantity: LiveData<ProductWithQuantity> get() = _productWithQuantity

    val isInvalidCount: LiveData<Boolean> =
        _productWithQuantity.map {
            it.quantity.value == 0
        }

    private val _mostRecentProduct: MutableLiveData<Product> = MutableLiveData()
    val mostRecentProduct: LiveData<Product> get() = _mostRecentProduct

    private val _mostRecentProductVisibility: MutableLiveData<Boolean> = MutableLiveData(false)
    val mostRecentProductVisibility: MutableLiveData<Boolean> get() = _mostRecentProductVisibility

    private val _addCartComplete = MutableSingleLiveData<Unit>()
    val addCartComplete: SingleLiveData<Unit> get() = _addCartComplete
    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            _error.value = true
            _errorMsg.setValue(throwable.message.toString())
        }

    init {
        loadProduct()
        addToRecentProduct()
    }

    fun loadProduct() {
        viewModelScope.launch(coroutineExceptionHandler) {
            productRepository.find(productId).onSuccess {
                _error.value = false
                _productWithQuantity.value = ProductWithQuantity(product = it)
            }
        }
    }

    override fun plusCount(productId: Long) {
        _productWithQuantity.value?.let {
            _productWithQuantity.value = it.inc()
        }
    }

    override fun minusCount(productId: Long) {
        _productWithQuantity.value?.let {
            _productWithQuantity.value = it.dec()
        }
    }

    fun addProductToCart() {
        viewModelScope.launch {
            _productWithQuantity.value?.let { productWithQuantity ->
                with(productWithQuantity) {
                    cartRepository.addProductToCart(this.product.id, this.quantity.value)
                        .onSuccess {
                            loadProduct()
                            _addCartComplete.setValue(Unit)
                        }
                }
            }
        }
    }

    private fun addToRecentProduct() {
        loadMostRecentProduct(productId)
        viewModelScope.launch {
            recentProductRepository.insert(productId)
        }
    }

    private fun loadMostRecentProduct(productId: Long) {
        viewModelScope.launch {
            recentProductRepository.findMostRecentProduct().onSuccess { recentProduct ->
                productRepository.find(recentProduct.productId).onSuccess { product ->
                    _error.value = false
                    _mostRecentProduct.value = product
                    if (!lastSeenProductState) return@launch
                    setMostRecentVisibility(product.id, productId)
                }.onFailure {
                    _error.value = true
                    _mostRecentProductVisibility.value = false
                    _errorMsg.setValue(it.toString())
                }
            }
        }
    }

    private fun setMostRecentVisibility(
        mostRecentProductId: Long,
        currentProductId: Long,
    ) {
        _mostRecentProductVisibility.value = (mostRecentProductId != currentProductId)
    }
}
