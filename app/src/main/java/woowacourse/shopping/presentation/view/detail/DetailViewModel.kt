package woowacourse.shopping.presentation.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.presentation.model.ProductUiModel
import woowacourse.shopping.presentation.model.toCartItem
import woowacourse.shopping.presentation.model.toUiModel
import woowacourse.shopping.presentation.view.ItemCounterListener

class DetailViewModel(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
) : ViewModel(),
    ItemCounterListener {
    private val _saveState = MutableLiveData<Unit>()
    val saveState: LiveData<Unit> = _saveState

    private val _amount = MutableLiveData<Int>(1)
    val amount: LiveData<Int> = _amount

    private val _product = MutableLiveData<ProductUiModel>()
    val product: LiveData<ProductUiModel> = _product

    private val _lastViewedProduct = MutableLiveData<ProductUiModel>()
    val lastViewedProduct: LiveData<ProductUiModel> = _lastViewedProduct

    fun decreaseAmount() {
        val current = _amount.value ?: 1
        if (current > 1) {
            _amount.value = current - 1
        }
    }

    fun increaseAmount() {
        val current = _amount.value ?: 1
        _amount.value = current + 1
    }

    fun fetchProduct(product: ProductUiModel) {
        _product.postValue(product)
        _amount.postValue(1)
    }

    fun addCartItem() {
        val product = _product.value ?: return
        if (product.cartId == 0L) {
            cartRepository.addCartItem(product.toCartItem()) {
                _saveState.postValue(Unit)
            }
        } else {
            val totalAmount = (_product.value?.amount ?: 0) + (_amount.value ?: 0)
            cartRepository.updateCartItemQuantity(
                cartId = product.cartId,
                quantity = totalAmount,
            ) {
                _saveState.postValue(Unit)
            }
        }
    }

    fun loadProductById(productId: Long) {
        productRepository.getProductById(productId) { product ->
            product?.let {
                fetchProduct(it.toUiModel())
            }
        }
    }

    fun fetchLastViewedProduct(currentProductId: Long) {
        productRepository.loadLastViewedProduct(currentProductId) { product ->
            product?.let { _lastViewedProduct.postValue(it.toUiModel()) }
        }
    }

    override fun increase(product: ProductUiModel) {
        increaseAmount()
    }

    override fun decrease(product: ProductUiModel) {
        decreaseAmount()
    }

    companion object {
        fun factory(
            cartRepository: CartRepository,
            productRepository: ProductRepository,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras,
                ): T = DetailViewModel(cartRepository, productRepository) as T
            }
    }
}
