package woowacourse.shopping.presentation.shopping.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.usecase.CreateCartProductUseCase
import woowacourse.shopping.presentation.base.BaseViewModelFactory
import woowacourse.shopping.presentation.cart.toDomain
import woowacourse.shopping.presentation.shopping.toCartUiModel
import woowacourse.shopping.presentation.util.MutableSingleLiveData
import woowacourse.shopping.presentation.util.SingleLiveData

class ProductDetailViewModel(
    private val createCartUseCase: CreateCartProductUseCase,
    private val productRepository: ProductRepository,
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

    fun loadProduct(id: Long) {
        productRepository.saveRecentProduct(id)
        productRepository.findProductById(id).onSuccess {
            _uiState.value = uiState.value?.copy(cartProductUi = it.toCartUiModel())
        }.onFailure {
            _errorEvent.setValue(ProductDetailErrorEvent.LoadProduct)
        }
    }

    fun refreshCartProduct() {
        val id = _uiState.value?.cartProductUi?.product?.id ?: return
        loadProduct(id)
    }

    override fun increaseProductCount(id: Long) {
        val newUiState = uiState.value?.increaseProductCount() ?: return
        _uiState.value = newUiState
    }

    override fun decreaseProductCount(id: Long) {
        if (uiState.value?.canDecreaseProductCount() != true) {
            return _errorEvent.setValue(ProductDetailErrorEvent.DecreaseCartCount)
        }
        val newUiState = uiState.value?.decreaseProductCount() ?: return
        _uiState.value = newUiState
    }

    override fun addCartProduct() {
        val cartProduct = uiState.value?.cartProductUi ?: return
        createCartUseCase(cartProduct.product.id, cartProduct.count).onSuccess {
            _addCartEvent.setValue(Unit)
            _updateCartEvent.setValue(Unit)
        }.onFailure {
            _errorEvent.setValue(ProductDetailErrorEvent.AddCartProduct)
        }
    }

    fun navigateToRecentProduct() {
        val recentId = _uiState.value?.recentProduct?.id ?: return
        productRepository.saveRecentProduct(recentId).onSuccess {
            _recentProductEvent.setValue(recentId)
        }.onFailure {
            _errorEvent.setValue(ProductDetailErrorEvent.SaveRecentProduct)
        }
    }

    private fun loadRecentProduct() {
        productRepository.loadRecentProducts(1).onSuccess {
            if (it.isEmpty()) return
            _uiState.value = uiState.value?.copy(recentProduct = it.first())
        }.onFailure {
            _errorEvent.setValue(ProductDetailErrorEvent.LoadProduct)
        }
    }

    companion object {
        fun factory(
            createCartUseCase: CreateCartProductUseCase,
            productRepository: ProductRepository,
        ): ViewModelProvider.Factory {
            return BaseViewModelFactory {
                ProductDetailViewModel(
                    createCartUseCase,
                    productRepository,
                )
            }
        }
    }
}

private fun ProductDetailUiState.increaseProductCount(): ProductDetailUiState =
    copy(cartProductUi = cartProductUi.copy(count = cartProductUi.toDomain().increaseCount().count))

private fun ProductDetailUiState.decreaseProductCount(): ProductDetailUiState =
    copy(cartProductUi = cartProductUi.copy(count = cartProductUi.toDomain().decreaseCount().count))

private fun ProductDetailUiState.canDecreaseProductCount(): Boolean =
    cartProductUi.toDomain().canDecreaseCount()
