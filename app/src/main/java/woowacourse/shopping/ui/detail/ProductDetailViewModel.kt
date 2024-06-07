package woowacourse.shopping.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import woowacourse.shopping.common.Event
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.ui.products.adapter.type.ProductUiModel
import woowacourse.shopping.ui.utils.AddCartQuantityBundle

class ProductDetailViewModel(
    private val productId: Int,
    private val productRepository: ProductRepository,
    private val recentProductRepository: RecentProductRepository,
    private val cartRepository: CartRepository,
    private val lastSeenProductVisible: Boolean,
) : ViewModel() {
    private val _productUiModel = MutableLiveData<ProductUiModel>()
    val productUiModel: LiveData<ProductUiModel> get() = _productUiModel

    private val _productLoadError = MutableLiveData<Event<Unit>>()
    val productLoadError: LiveData<Event<Unit>> get() = _productLoadError

    private val _isSuccessAddCart = MutableLiveData<Event<Boolean>>()
    val isSuccessAddCart: LiveData<Event<Boolean>> get() = _isSuccessAddCart

    val addCartQuantityBundle: LiveData<AddCartQuantityBundle> =
        _productUiModel.map {
            AddCartQuantityBundle(
                productId = it.productId,
                quantity = it.quantity,
                onIncreaseProductQuantity = { increaseQuantity() },
                onDecreaseProductQuantity = { decreaseQuantity() },
            )
        }

    private val _lastRecentProduct = MutableLiveData<LastRecentProductUiModel>()
    val lastRecentProduct: LiveData<LastRecentProductUiModel> get() = _lastRecentProduct

    val isVisibleLastRecentProduct: LiveData<Boolean> =
        _lastRecentProduct.map { !lastSeenProductVisible && it.productId != productId }

    init {
        loadLastRecentProduct()
    }

    fun loadProduct() =
        viewModelScope.launch {
            productRepository.find(productId)
                .onSuccess { product ->
                    _productUiModel.value = product.toProductUiModel(this)
                    saveRecentProduct()
                }.onFailure {
                    setError()
                }
        }

    private suspend fun Product.toProductUiModel(scope: CoroutineScope): ProductUiModel {
        val cartItemDeferred = scope.async { cartRepository.findByProductId(id) }
        val cartItem = cartItemDeferred.await().getOrNull() ?: return ProductUiModel.from(this)
        return ProductUiModel.from(this, cartItem.quantity)
    }

    private fun loadLastRecentProduct() =
        viewModelScope.launch {
            val lastRecentProduct = recentProductRepository.findLastOrNull().getOrNull() ?: return@launch
            productRepository.find(lastRecentProduct.product.id)
                .onSuccess { product ->
                    _lastRecentProduct.value = LastRecentProductUiModel(product.id, product.name)
                }.onFailure {
                    setError()
                }
        }

    private fun increaseQuantity() {
        var quantity = _productUiModel.value?.quantity ?: return
        _productUiModel.value = _productUiModel.value?.copy(quantity = ++quantity)
    }

    private fun decreaseQuantity() {
        var quantity = _productUiModel.value?.quantity ?: return
        _productUiModel.value = _productUiModel.value?.copy(quantity = --quantity)
    }

    fun addCartProduct() =
        viewModelScope.launch {
            val productUiModel = _productUiModel.value ?: return@launch
            val cartItem = cartRepository.findByProductId(productUiModel.productId).getOrNull()

            val addCartResult =
                if (cartItem == null) {
                    cartRepository.add(productId, productUiModel.quantity)
                } else {
                    cartRepository.changeQuantity(cartItem.id, productUiModel.quantity)
                }

            addCartResult
                .onSuccess { _isSuccessAddCart.value = Event(true) }
                .onFailure { _isSuccessAddCart.value = Event(false) }
        }

    private fun setError() {
        _productLoadError.value = Event(Unit)
    }

    override fun onCleared() {
        super.onCleared()
        saveRecentProduct()
    }

    private fun saveRecentProduct() =
        viewModelScope.launch {
            val product = productRepository.find(productId).getOrNull() ?: return@launch
            recentProductRepository.save(product)
        }
}
