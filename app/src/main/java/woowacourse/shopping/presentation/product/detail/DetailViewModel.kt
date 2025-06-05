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
import woowacourse.shopping.presentation.util.SingleLiveEvent

class DetailViewModel(
    private val productsRepository: ProductsRepository,
    private val cartItemRepository: CartItemRepository,
    private val viewedRepository: ViewedItemRepository,
) : ViewModel() {
    private val _product = MutableLiveData<ProductUiModel>()
    val product: LiveData<ProductUiModel> = _product

    private val _uiState = SingleLiveEvent<CartEvent>()
    val uiState: LiveData<CartEvent> = _uiState

    private val _lastViewed = MutableLiveData<ProductUiModel?>()
    val lastViewed: LiveData<ProductUiModel?> = _lastViewed

    fun setProduct(
        id: Long,
        onInserted: () -> Unit = {},
    ) {
        productsRepository.getProductById(id) { result ->
            result
                .onSuccess { product ->
                    val loadedProduct = product.copy(quantity = 1)
                    _product.postValue(loadedProduct)

                    viewedRepository.insertViewedItem(loadedProduct) {
                        onInserted()
                    }
                }
        }
    }

    fun addToCart() {
        val product = _product.value ?: return
        cartItemRepository.addCartItemQuantity(product.id, product.quantity) { result ->
            result
                .onSuccess {
                    _uiState.postValue(CartEvent.ADD_TO_CART_SUCCESS)
                }
                .onFailure {
                    _uiState.postValue(CartEvent.ADD_TO_CART_FAILURE)
                }
        }
    }

    fun loadLastViewedItem(currentProductId: Long) {
        viewedRepository.getLastViewedItem { lastViewedItem ->
            val filtered = if (lastViewedItem?.id == currentProductId) null else lastViewedItem
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
        val FACTORY: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    DetailViewModel(
                        productsRepository = RepositoryProvider.productsRepository,
                        cartItemRepository = RepositoryProvider.cartItemRepository,
                        viewedRepository = RepositoryProvider.viewedItemRepository,
                    )
                }
            }
    }
}
