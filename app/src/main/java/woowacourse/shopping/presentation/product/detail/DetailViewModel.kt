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
import woowacourse.shopping.mapper.toUiModel
import woowacourse.shopping.presentation.product.catalog.ProductUiModel

class DetailViewModel(
    private val productsRepository: ProductsRepository,
    private val cartItemRepository: CartItemRepository,
    private val viewedRepository: ViewedItemRepository,
) : ViewModel() {
    private val _product = MutableLiveData<ProductUiModel>()
    val product: LiveData<ProductUiModel> = _product

    private val _uiState = MutableLiveData<CartUiState>()
    val uiState: LiveData<CartUiState> = _uiState

    private val _lastViewed = MutableLiveData<ProductUiModel?>()
    val lastViewed: LiveData<ProductUiModel?> = _lastViewed

    fun setProduct(
        id: Int,
        onInserted: () -> Unit = {},
    ) {
        productsRepository.getProductById(id) { result ->
            result
                .onSuccess { product ->
                    _product.postValue(product.toUiModel())
                }
        }
        _product.value?.let {
            viewedRepository.insertViewedItem(it) {
                onInserted()
            }
        }
    }

    fun addToCart() {
//        val item = _product.value ?: return
//        if (item.quantity <= 0) return
//
//        cartItemRepository.findCartItem(item) { exist ->
//            val updated =
//                exist?.let {
//                    it.copy(quantity = it.quantity + item.quantity).toUiModel()
//                } ?: item
//
//            val callback = { _uiState.postValue(CartUiState.SUCCESS) }
//
//            if (exist != null) {
//                cartItemRepository.updateCartItem(updated, callback)
//            } else {
//                cartItemRepository.insertCartItem(updated, callback)
//            }
//        }
    }

    fun loadLastViewedItem(currentProductId: Int) {
        viewedRepository.getLastViewedItem { lastViewedItem ->
            val filtered = if (lastViewedItem?.id == currentProductId) null else lastViewedItem
            _lastViewed.postValue(filtered)
        }
    }

    fun increaseQuantity() = updateQuantity(+1)

    fun decreaseQuantity() = updateQuantity(-1)

    private fun updateQuantity(diff: Int) {
        val current = _product.value ?: return
        val newQty = (current.quantity + diff).coerceAtLeast(0)
        _product.postValue(current.copy(quantity = newQty))
    }

    companion object {
        val FACTORY: ViewModelProvider.Factory = viewModelFactory {
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
