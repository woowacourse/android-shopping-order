package woowacourse.shopping.presentation.shopping.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ShoppingRepository
import woowacourse.shopping.presentation.base.BaseViewModelFactory
import woowacourse.shopping.presentation.cart.toUiModel
import woowacourse.shopping.presentation.shopping.toCartUiModel
import woowacourse.shopping.presentation.util.MutableSingleLiveData
import woowacourse.shopping.presentation.util.SingleLiveData

class ProductDetailViewModel(
    private val cartRepository: CartRepository,
    private val shoppingRepository: ShoppingRepository,
) : ViewModel(), DetailProductListener {
    private val _uiState = MutableLiveData<ProductDetailUiState>(ProductDetailUiState.init())
    val uiState: LiveData<ProductDetailUiState> get() = _uiState

    private val _updateCartEvent = MutableSingleLiveData<Unit>()
    val updateCartEvent: SingleLiveData<Unit> get() = _updateCartEvent

    private val _addCartEvent = MutableSingleLiveData<Unit>()
    val addCartEvent: SingleLiveData<Unit> get() = _addCartEvent

    private val _recentProductEvent = MutableSingleLiveData<Long>()
    val recentProductEvent: SingleLiveData<Long> get() = _recentProductEvent

    private val _errorEvent: MutableSingleLiveData<ProductDetailErrorEvent> =
        MutableSingleLiveData()
    val errorEvent: SingleLiveData<ProductDetailErrorEvent> get() = _errorEvent

    init {
        loadRecentProduct()
    }

    private fun loadRecentProduct() {
        viewModelScope.launch {
            shoppingRepository.recentProducts(1)
                .onSuccess { recentProducts ->
                    if (recentProducts.isEmpty()) return@launch
                    _uiState.value = uiState.value?.updateRecentProductWithFirstOf(recentProducts)
                }.onFailure {
                    _errorEvent.setValue(ProductDetailErrorEvent.LoadCartProduct)
                }
        }
    }

    suspend fun refreshDetailProduct() {
        val id = _uiState.value?.cartProduct?.product?.id ?: return
        loadCartProduct(id)
        shoppingRepository.saveRecentProduct(id)
    }

    suspend fun loadCartProduct(id: Long) {
        cartRepository.filterCartProducts(listOf(id))
            .onSuccess {
                if (it.isEmpty()) return loadProduct(id)
                _uiState.value = uiState.value?.copy(cartProduct = it.first().toUiModel())
            }.onFailure {
                _errorEvent.setValue(ProductDetailErrorEvent.LoadCartProduct)
            }
    }

    override fun increaseProductCount(id: Long) {
        val newUiState = uiState.value?.increaseProductCount(INCREMENT_AMOUNT) ?: return
        _uiState.value = newUiState
    }

    override fun decreaseProductCount(id: Long) {
        if (uiState.value?.canDecreaseProductCount() != true) {
            return _errorEvent.setValue(ProductDetailErrorEvent.DecreaseCartCount)
        }
        val newUiState = uiState.value?.decreaseProductCount(INCREMENT_AMOUNT) ?: return
        _uiState.value = newUiState
    }

    override fun addCartProduct() {
        viewModelScope.launch {
            val cartProduct = uiState.value?.cartProduct ?: return@launch
            cartRepository.updateCartProduct(cartProduct.product.id, cartProduct.count).onSuccess {
                _addCartEvent.setValue(Unit)
                _updateCartEvent.setValue(Unit)
            }.onFailure {
                _errorEvent.setValue(ProductDetailErrorEvent.AddCartProduct)
            }
        }
    }

    fun navigateToRecentProduct() {
        val recentId = _uiState.value?.recentProduct?.id ?: return
        viewModelScope.launch {
            shoppingRepository.saveRecentProduct(recentId).onSuccess {
                _recentProductEvent.setValue(recentId)
            }.onFailure {
                _errorEvent.setValue(ProductDetailErrorEvent.SaveRecentProduct)
            }
        }
    }

    private fun loadProduct(id: Long) {
        viewModelScope.launch {
            shoppingRepository.productById(id).onSuccess {
                _uiState.value = uiState.value?.copy(cartProduct = it.toCartUiModel())
            }.onFailure {
                _errorEvent.setValue(ProductDetailErrorEvent.LoadCartProduct)
            }
        }
    }

    companion object {
        private const val INCREMENT_AMOUNT = 1

        fun factory(
            cartRepository: CartRepository,
            shoppingRepository: ShoppingRepository,
        ): ViewModelProvider.Factory {
            return BaseViewModelFactory {
                ProductDetailViewModel(
                    cartRepository,
                    shoppingRepository,
                )
            }
        }
    }
}
