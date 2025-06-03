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

    fun loadProduct(id: Long) {
        productRepository.findProductById(id) { product ->
            cartRepository.findCartItemByProductId(id) { cartItem ->
                if (cartItem == null) {
                    _product.postValue(product?.toProductUiModel()?.copy(quantity = MIN_QUANTITY))
                } else {
                    _product.postValue(cartItem.toProductUiModel().copy(quantity = MIN_QUANTITY))
                }
                productRepository.loadRecentProducts(1) { recentProducts ->
                    if (recentProducts.isNotEmpty()) {
                        _lastViewedProduct.postValue(recentProducts.first().toProductUiModel())
                    }
                    if (product != null) productRepository.addRecentProduct(product)
                }
            }
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
        val product = _product.value ?: return
        cartRepository.findCartItemByProductId(product.id) { cartItem ->
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

    companion object {
        private const val MIN_QUANTITY = 1

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
