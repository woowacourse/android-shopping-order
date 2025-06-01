package woowacourse.shopping.presentation.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import woowacourse.shopping.RepositoryProvider
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.presentation.model.ProductUiModel
import woowacourse.shopping.presentation.model.toCartItem
import woowacourse.shopping.presentation.model.toProduct
import woowacourse.shopping.presentation.model.toUiModel

class DetailViewModel(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
) : ViewModel() {
    private val _saveState = MutableLiveData<Unit>()
    val saveState: LiveData<Unit> = _saveState

    private val _product = MutableLiveData<ProductUiModel>()
    val product: LiveData<ProductUiModel> = _product

    private val _lastViewedProduct = MutableLiveData<ProductUiModel>()
    val lastViewedProduct: LiveData<ProductUiModel> = _lastViewedProduct

    fun fetchProduct(product: ProductUiModel) {
        _product.postValue(product.copy(quantity = 1))
    }

    fun addRecentProduct(product: ProductUiModel) {
        productRepository.addRecentProduct(product.toProduct())
    }

    fun increaseQuantity() {
        val currentQuantity = _product.value?.quantity ?: 1
        _product.postValue(_product.value?.copy(quantity = currentQuantity + 1))
    }

    fun decreaseQuantity() {
        val currentQuantity = _product.value?.quantity ?: 1
        _product.postValue(_product.value?.copy(quantity = (currentQuantity - 1).coerceAtLeast(1)))
    }

    fun addCartItem() {
        val product = _product.value ?: return
        if (product.cartId == 0L) {
            cartRepository.addCartItem(product.toCartItem()) {
                _saveState.postValue(Unit)
            }
        } else {
            cartRepository.updateCartItemQuantity(
                cartId = product.cartId,
                quantity = _product.value?.quantity ?: 1,
            ) {
                _saveState.postValue(Unit)
            }
        }
    }

    fun fetchLastViewedProduct() {
        productRepository.loadRecentProducts(1) { recentProducts ->
            if (recentProducts.isNotEmpty()) {
                _lastViewedProduct.postValue(recentProducts.first().toUiModel())
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras,
                ): T {
                    val cartRepository = RepositoryProvider.cartRepository
                    val productRepository = RepositoryProvider.productRepository
                    return DetailViewModel(cartRepository, productRepository) as T
                }
            }
    }
}
