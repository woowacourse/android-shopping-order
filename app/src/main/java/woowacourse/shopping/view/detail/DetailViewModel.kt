package woowacourse.shopping.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.model.toProduct
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.util.Event
import woowacourse.shopping.view.cart.QuantityEventListener
import woowacourse.shopping.view.state.DetailUiEvent
import woowacourse.shopping.view.state.ProductDetailUiState

class DetailViewModel(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
    private val recentProductRepository: RecentProductRepository,
    private val productId: Int,
) : ViewModel(), DetailEventListener, QuantityEventListener {
    private val _productDetailUiState: MutableLiveData<ProductDetailUiState> =
        MutableLiveData(ProductDetailUiState())
    val productDetailUiState: LiveData<ProductDetailUiState>
        get() = _productDetailUiState

    private val _detailUiEvent: MutableLiveData<Event<DetailUiEvent>> = MutableLiveData()
    val detailUiEvent: LiveData<Event<DetailUiEvent>>
        get() = _detailUiEvent

    init {
        loadProduct()
    }

    override fun addQuantity(cartItemId: Int) {
        _productDetailUiState.value =
            productDetailUiState.value?.copy(
                quantity = productDetailUiState.value?.quantity?.plus(1) ?: return,
            )
    }

    override fun subtractQuantity(cartItemId: Int) {
        _productDetailUiState.value =
            productDetailUiState.value?.copy(
                quantity = productDetailUiState.value?.quantity?.minus(1) ?: return,
            )
    }

    override fun addToCart() {
        viewModelScope.launch {
            val uiState = productDetailUiState.value ?: return@launch
            val targetCartItem = uiState.cartItems.firstOrNull { it.productId == productId }
            if (targetCartItem == null) {
                cartRepository.addCartItem(productId, uiState.quantity)
                return@launch
            }
            val result = cartRepository.updateCartItem(targetCartItem.cartItemId, uiState.quantity)
                .getOrNull()
            if (result == null) {
                notifyError()
                return@launch
            }
            _detailUiEvent.value = Event(DetailUiEvent.NavigateToCart)
        }
    }

    override fun navigateToRecentProduct() {
        _detailUiEvent.value =
            Event(
                DetailUiEvent.NavigateToRecentProduct(
                    productDetailUiState.value?.lastlyViewedProduct?.productId ?: return,
                ),
            )
    }

    override fun navigateBackToHome() {
        _detailUiEvent.value = Event(DetailUiEvent.NavigateBack)
    }

    fun saveRecentProduct(mostRecentProductClicked: Boolean) {
        viewModelScope.launch {
            if (mostRecentProductClicked) return@launch
            recentProductRepository.save(
                productDetailUiState.value?.product?.toProduct() ?: return@launch
            )
        }
    }

    private fun loadProduct() {
        viewModelScope.launch {
            val result = productRepository.getProductById(id = productId).getOrNull()
            val entireCartItems = cartRepository.getEntireCartData().getOrNull() ?: emptyList()
            if (result == null) {
                notifyError()
                return@launch
            }

            _productDetailUiState.value =
                productDetailUiState.value?.copy(
                    isLoading = false,
                    product = result.productItemDomain,
                    lastlyViewedProduct = recentProductRepository.findMostRecentProduct(),
                    quantity = 1,
                    cartItems = entireCartItems,
                )
        }
    }

    private fun notifyError() {
        _detailUiEvent.value = Event(DetailUiEvent.Error)
    }
}
