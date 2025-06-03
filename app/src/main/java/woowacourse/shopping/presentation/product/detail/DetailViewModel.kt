package woowacourse.shopping.presentation.product.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import woowacourse.shopping.RepositoryProvider
import woowacourse.shopping.domain.repository.CartItemRepository
import woowacourse.shopping.domain.repository.ProductsRepository
import woowacourse.shopping.domain.repository.ViewedItemRepository
import woowacourse.shopping.presentation.product.catalog.ProductUiModel
import woowacourse.shopping.presentation.product.detail.CartEvent.AddItemFailure
import woowacourse.shopping.presentation.product.detail.CartEvent.AddItemSuccess
import woowacourse.shopping.presentation.util.SingleLiveEvent

class DetailViewModel(
    private val productsRepository: ProductsRepository,
    private val cartItemRepository: CartItemRepository,
    private val viewedRepository: ViewedItemRepository,
    private val productId: Long,
) : ViewModel() {
    private val _product = MutableLiveData<ProductUiModel>()
    val product: LiveData<ProductUiModel> = _product

    private val _cartEvent = SingleLiveEvent<CartEvent>()
    val cartEvent: LiveData<CartEvent> = _cartEvent

    private val _lastViewed = MutableLiveData<ProductUiModel?>()
    val lastViewed: LiveData<ProductUiModel?> = _lastViewed

    private val _productInserted = MutableLiveData<Boolean>()
    val productInserted: LiveData<Boolean> = _productInserted

    fun setProduct() {
        productsRepository.getProductById(productId) { result ->
            result
                .onSuccess { product ->
                    val loadedProduct = product.copy(quantity = 1)
                    _product.postValue(loadedProduct)

                    viewedRepository.insertViewedItem(loadedProduct) {
                        _productInserted.postValue(true)
                    }
                }
                .onFailure {
                    _productInserted.postValue(false)
                }
        }
    }

    fun addToCart() {
        val product = _product.value ?: return
        cartItemRepository.addCartItemQuantity(product.id, product.quantity) { result ->
            result
                .onSuccess {
                    _cartEvent.postValue(AddItemSuccess)
                }
                .onFailure {
                    _cartEvent.postValue(AddItemFailure)
                }
        }
    }

    fun loadLastViewedItem() {
        viewedRepository.getLastViewedItem { lastViewedItem ->
            val filtered = if (lastViewedItem?.id == productId) null else lastViewedItem
            _lastViewed.postValue(filtered)
        }
    }

    fun increaseQuantity() = updateQuantity(+1)

    fun decreaseQuantity() = updateQuantity(-1)

    private fun updateQuantity(quantity: Int) {
        val current = _product.value ?: return
        val newQuantity = (current.quantity + quantity).coerceAtLeast(0)
        _product.value = current.copy(quantity = newQuantity)
    }

    companion object {
        fun provideFactory(
            productId: Long,
        ): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    DetailViewModel(
                        productsRepository = RepositoryProvider.productsRepository,
                        cartItemRepository = RepositoryProvider.cartItemRepository,
                        viewedRepository = RepositoryProvider.viewedItemRepository,
                        productId = productId
                    )
                }
            }
        }

    }
}
