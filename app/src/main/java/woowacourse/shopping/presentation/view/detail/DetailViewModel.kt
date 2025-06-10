package woowacourse.shopping.presentation.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.data.repository.RepositoryProvider
import woowacourse.shopping.presentation.model.ProductUiModel
import woowacourse.shopping.presentation.model.toCartItem
import woowacourse.shopping.presentation.model.toProductUiModel

class DetailViewModel(
    private val productId: Long,
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
) : ViewModel() {
    private val _saveEvent = MutableLiveData<Unit>()
    val saveEvent: LiveData<Unit> = _saveEvent

    private val _product = MutableLiveData<ProductUiModel>()
    val product: LiveData<ProductUiModel> = _product

    private val _lastViewedProduct = MutableLiveData<ProductUiModel>()
    val lastViewedProduct: LiveData<ProductUiModel> = _lastViewedProduct

    init {
        loadProduct()
    }

    fun loadProduct() {
        viewModelScope.launch {
            val product = productRepository.findProductById(productId)
            val cartItem = cartRepository.loadCartItemByProductId(productId)
            if (cartItem == null) {
                _product.postValue(product?.toProductUiModel()?.copy(quantity = MIN_QUANTITY))
            } else {
                _product.postValue(cartItem.toProductUiModel().copy(quantity = MIN_QUANTITY))
            }
            val recentProduct = productRepository.getMostRecentProduct()
            _lastViewedProduct.postValue(recentProduct?.toProductUiModel())
            if (product != null) productRepository.addRecentProduct(product)
        }
    }

    fun increaseQuantity() {
        val currentQuantity = _product.value?.quantity ?: MIN_QUANTITY
        _product.postValue(_product.value?.copy(quantity = currentQuantity + 1))
    }

    fun decreaseQuantity() {
        val currentQuantity = _product.value?.quantity ?: MIN_QUANTITY
        _product.postValue(_product.value?.copy(quantity = (currentQuantity - 1).coerceAtLeast(MIN_QUANTITY)))
    }

    fun addToCart() {
        viewModelScope.launch {
            val product = _product.value ?: return@launch
            cartRepository.addOrUpdateCartItem(product.toCartItem())
            _saveEvent.postValue(Unit)
        }
    }

    companion object {
        private const val MIN_QUANTITY = 1

        @Suppress("UNCHECKED_CAST")
        fun factory(productId: Long): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras,
                ): T {
                    val cartRepository = RepositoryProvider.cartRepository
                    val productRepository = RepositoryProvider.productRepository
                    return DetailViewModel(productId, cartRepository, productRepository) as T
                }
            }
    }
}
