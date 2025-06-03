package woowacourse.shopping.presentation.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import woowacourse.shopping.data.RepositoryProvider
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.presentation.model.ProductUiModel
import woowacourse.shopping.presentation.model.toCartItem
import woowacourse.shopping.presentation.model.toProduct
import woowacourse.shopping.presentation.model.toProductUiModel

class DetailViewModel(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
) : ViewModel() {
    private val _saveEvent = MutableLiveData<Unit>()
    val saveEvent: LiveData<Unit> = _saveEvent

    private val _product = MutableLiveData<ProductUiModel>()
    val product: LiveData<ProductUiModel> = _product

    private val _lastViewedProduct = MutableLiveData<ProductUiModel>()
    val lastViewedProduct: LiveData<ProductUiModel> = _lastViewedProduct

    fun fetchProduct(id: Long) {
        productRepository.findProductById(id) { product ->
            cartRepository.loadAllCartItems { cartItems ->
                val cartItem = cartItems.find { cartItem -> cartItem.product.id == id }
                if (cartItem == null) {
                    _product.postValue(product?.toProductUiModel()?.copy(quantity = 1))
                } else {
                    _product.postValue(cartItem.toProductUiModel().copy(quantity = 1))
                }
            }
        }
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

    fun addToCart() {
        val product = _product.value ?: return
        cartRepository.loadAllCartItems { cartItems ->
            val cartItem = cartItems.find { cartItem -> cartItem.product.id == product.id }
            if (cartItem == null) {
                cartRepository.addCartItem(product.toCartItem()) {
                    _saveEvent.postValue(Unit)
                }
            } else {
                cartRepository.updateCartItemQuantity(
                    cartId = product.cartId,
                    quantity = cartItem.quantity + product.quantity,
                ) {
                    _saveEvent.postValue(Unit)
                }
            }
        }
    }

    fun fetchLastViewedProduct() {
        productRepository.loadRecentProducts(1) { recentProducts ->
            if (recentProducts.isNotEmpty()) {
                _lastViewedProduct.postValue(recentProducts.first().toProductUiModel())
            }
        }
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
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
