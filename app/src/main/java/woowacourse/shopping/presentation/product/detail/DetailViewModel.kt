package woowacourse.shopping.presentation.product.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.launch
import woowacourse.shopping.RepositoryProvider
import woowacourse.shopping.domain.repository.CartItemsRepository
import woowacourse.shopping.domain.repository.ProductsRepository
import woowacourse.shopping.domain.repository.ViewedItemRepository
import woowacourse.shopping.presentation.product.catalog.ProductUiModel
import woowacourse.shopping.presentation.product.catalog.toDomain
import woowacourse.shopping.presentation.product.catalog.toUiModel
import woowacourse.shopping.presentation.util.SingleLiveEvent

class DetailViewModel(
    private val productsRepository: ProductsRepository,
    private val cartItemRepository: CartItemsRepository,
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
        viewModelScope.launch {
            val product =
                productsRepository.getProductById(id)
                    .mapCatching { it.toUiModel() }
                    .getOrNull()
            val loadedProduct = product?.copy(quantity = 1)
            _product.value = loadedProduct

            loadedProduct?.let {
                viewedRepository.insertViewedItem(it.toDomain())
            }
        }
    }

    fun addToCart() {
        viewModelScope.launch {
            val product = _product.value ?: return@launch
            cartItemRepository.addCartItemQuantity(product.id, product.quantity)
                .onFailure {
                    _uiState.postValue(CartEvent.ADD_TO_CART_FAILURE)
                }
        }
    }

    fun loadLastViewedItem(currentProductId: Long) {
        viewModelScope.launch {
            val item =
                viewedRepository.getLastViewedItem()
                    .mapCatching { it?.toUiModel() }
                    .getOrNull()

            item?.let {
                val filtered = if (it.id == currentProductId) null else it
                _lastViewed.value = filtered
            }
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
