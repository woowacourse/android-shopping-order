package woowacourse.shopping.ui.detail.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.ui.detail.action.DetailNavigationActions
import woowacourse.shopping.ui.detail.action.DetailNotifyingActions
import woowacourse.shopping.ui.detail.listener.DetailClickListener
import woowacourse.shopping.ui.event.Event
import woowacourse.shopping.ui.home.listener.QuantityClickListener
import woowacourse.shopping.ui.order.viewmodel.OrderViewModel.Companion.DESCENDING_SORT_ORDER
import woowacourse.shopping.ui.state.UiState

class DetailViewModel(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
    private val recentProductRepository: RecentProductRepository,
    private val productId: Int,
    private val isMostRecentProductClicked: Boolean,
) : ViewModel(), DetailClickListener, QuantityClickListener {
    private val _detailUiState = MutableLiveData<UiState<Product>>(UiState.Loading)
    val detailUiState: LiveData<UiState<Product>>
        get() = _detailUiState

    private val _product: MutableLiveData<Product> = MutableLiveData()
    val product: LiveData<Product>
        get() = _product

    private val _mostRecentProduct: MutableLiveData<RecentProduct?> = MutableLiveData()
    val mostRecentProduct: LiveData<RecentProduct?>
        get() = _mostRecentProduct

    private val _quantity = MutableLiveData<Int>()
    val quantity: LiveData<Int>
        get() = _quantity

    val totalPrice: LiveData<Int> =
        quantity.map { quantityValue ->
            product.value?.price?.times(quantityValue) ?: 0
        }

    private val _isMostRecentProductVisible = MutableLiveData<Boolean>()
    val isMostRecentProductVisible: LiveData<Boolean>
        get() = _isMostRecentProductVisible

    private val _detailNavigationActions = MutableLiveData<Event<DetailNavigationActions>>()
    val detailNavigationActions: LiveData<Event<DetailNavigationActions>>
        get() = _detailNavigationActions

    private val _detailNotifyingActions = MutableLiveData<Event<DetailNotifyingActions>>()
    val detailNotifyingActions: LiveData<Event<DetailNotifyingActions>>
        get() = _detailNotifyingActions

    init {
        viewModelScope.launch {
            loadProduct().join()
            saveRecentProduct(isMostRecentProductClicked)
        }
    }

    suspend fun saveRecentProduct(isMostRecentProductClicked: Boolean) {
        recentProductRepository.findMostRecentProduct()
            .onSuccess { mostRecentProduct ->
                _mostRecentProduct.value = mostRecentProduct
                val currentProduct = product.value ?: return@onSuccess
                recentProductRepository.save(currentProduct)
                updateRecentProductVisible(isMostRecentProductClicked)
            }.onFailure {
                _detailNotifyingActions.value = Event(DetailNotifyingActions.NotifyError)
            }
    }

    fun updateRecentProductVisible(isMostRecentProductClicked: Boolean) {
        _isMostRecentProductVisible.value =
            if (mostRecentProduct.value == null || product.value?.productId == mostRecentProduct.value?.productId) {
                false
            } else {
                !isMostRecentProductClicked
            }
    }

    private fun loadProduct() =
        viewModelScope.launch {
            productRepository.getProductById(productId)
                .onSuccess { product ->
                    _product.value = product
                    _quantity.value = DEFAULT_QUANTITY
                    _detailUiState.value = UiState.Success(product)
                }.onFailure {
                    _detailUiState.value = UiState.Error(it)
                }
        }

    private fun saveCartItem() {
        if (detailUiState.value is UiState.Success) {
            viewModelScope.launch {
                val totalQuantity = cartRepository.getCartTotalQuantity().getOrNull() ?: 0
                cartRepository.getCartItems(0, totalQuantity, DESCENDING_SORT_ORDER)
                    .onSuccess { cartItems ->
                        val cartItem =
                            cartItems.firstOrNull { cartItem ->
                                cartItem.product.productId == productId
                            }
                        val currentQuantity = cartItem?.quantity ?: 0
                        val quantity = quantity.value ?: 0

                        if (cartItem == null) {
                            cartRepository.addCartItem(productId, quantity)
                                .onFailure { throw IllegalArgumentException() }
                        } else {
                            cartRepository.updateCartItem(
                                cartItem.cartItemId,
                                quantity + currentQuantity,
                            ).onFailure {
                                _detailNotifyingActions.value =
                                    Event(DetailNotifyingActions.NotifyError)
                            }
                        }
                    }.onFailure {
                        _detailNotifyingActions.value = Event(DetailNotifyingActions.NotifyError)
                    }
            }
        }
    }

    override fun onPutInCartButtonClick() {
        saveCartItem()
        _detailNotifyingActions.value = Event(DetailNotifyingActions.NotifyPutInCartItem)
    }

    override fun onRecentProductClick() {
        _isMostRecentProductVisible.value = false
        _detailNavigationActions.value = Event(DetailNavigationActions.NavigateToRecentDetail)
    }

    override fun onQuantityPlusButtonClick(productId: Int) {
        _quantity.value = quantity.value?.plus(1)
    }

    override fun onQuantityMinusButtonClick(productId: Int) {
        _quantity.value = (quantity.value?.minus(1))?.coerceAtLeast(1)
    }

    override fun onFinishButtonClick() {
        _detailNavigationActions.value = Event(DetailNavigationActions.NavigateToBack)
    }

    companion object {
        private const val DEFAULT_QUANTITY = 1
    }
}
