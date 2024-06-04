package woowacourse.shopping.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.domain.model.ProductItemDomain
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
        _productDetailUiState.value = productDetailUiState.value?.copy(
            quantity = productDetailUiState.value?.quantity?.plus(1) ?: return
        )
    }

    override fun subtractQuantity(cartItemId: Int) {
        _productDetailUiState.value = productDetailUiState.value?.copy(
            quantity = productDetailUiState.value?.quantity?.minus(1) ?: return
        )
    }

    override fun addToCart() {
        val cartItems = productDetailUiState.value?.cartItems
        val productId = productDetailUiState.value?.product?.id ?: return
        val targetCartItem = cartItems?.firstOrNull { it.product.id == productId }
        if (targetCartItem == null) {
            cartRepository.addCartItem(
                productId = productId,
                quantity = productDetailUiState.value?.quantity ?: return,
                onSuccess = {
                    _detailUiEvent.value = Event(DetailUiEvent.NavigateToCart)
                },
                onFailure = {

                },
            )
            return
        }

        cartRepository.updateCartItem(
            cartItemId = targetCartItem.cartItemId,
            quantity = productDetailUiState.value?.quantity ?: return,
            onSuccess = {
                _detailUiEvent.value = Event(DetailUiEvent.NavigateToCart)
            },
            onFailure = {

            },
        )
    }

    override fun navigateToRecentProduct() {
        _detailUiEvent.value = Event(
            DetailUiEvent.NavigateToRecentProduct(
                productDetailUiState.value?.lastlyViewedProduct?.productId ?: return
            )
        )
    }

    override fun navigateBackToHome() {
        _detailUiEvent.value = Event(DetailUiEvent.NavigateBack)
    }

    private fun loadProduct() {
        productRepository.getProductById(
            id = productId,
            onSuccess = ::loadCartDataToProduct,
            onFailure = {

            },
        )
    }

    private fun loadCartDataToProduct(productItem: ProductItemDomain) {
        cartRepository.getCartTotalQuantity(
            onSuccess = { totalQuantity ->
                cartRepository.getCartItems(
                    page = 0,
                    size = totalQuantity,
                    sort = "asc",
                    onSuccess = { cart ->
                        _productDetailUiState.value = productDetailUiState.value?.copy(
                            isLoading = false,
                            product = productItem,
                            lastlyViewedProduct = recentProductRepository.findMostRecentProduct(),
                            quantity = 1,
                            cartItems = cart.cartItems
                        )
                    },
                    onFailure = {

                    },
                )
            },
            onFailure = {

            },
        )
    }
}
