package woowacourse.shopping.presentation.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.presentation.model.ProductUiModel
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

    private val _showLastViewedProduct = MediatorLiveData<Boolean>()
    val showLastViewedProduct: LiveData<Boolean> = _showLastViewedProduct

    init {
        _showLastViewedProduct.addSource(_product) { updateShowLastViewedProduct() }
        _showLastViewedProduct.addSource(_lastViewedProduct) { updateShowLastViewedProduct() }
    }

    private fun updateShowLastViewedProduct() {
        val currentProduct = _product.value
        val lastViewedProduct = _lastViewedProduct.value

        val canShow =
            currentProduct != null &&
                lastViewedProduct != null &&
                currentProduct.id != lastViewedProduct.id

        _showLastViewedProduct.value = canShow
    }

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
        val amountToAdd = _amount.value ?: 1
        val updatedAmount = product.amount + amountToAdd

        viewModelScope.launch {
            cartRepository
                .upsertCartItemQuantity(
                    productId = product.id,
                    cartId = if (product.cartId != 0L) product.cartId else null,
                    quantity = updatedAmount,
                ).onSuccess {
                    _product.postValue(product.copy(amount = updatedAmount))
                    _saveState.postValue(Unit)
                }
        }
    }

    fun loadProductById(productId: Long) {
        viewModelScope.launch {
            productRepository
                .getProductById(productId)
                .onSuccess { product ->
                    _product.postValue(product.toUiModel())
                }
        }
    }

    fun fetchLastViewedProduct(currentProductId: Long) {
        viewModelScope.launch {
            productRepository
                .loadLastViewedProduct(currentProductId)
                .onSuccess { product ->
                    _lastViewedProduct.postValue(product.toUiModel())
                }
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
