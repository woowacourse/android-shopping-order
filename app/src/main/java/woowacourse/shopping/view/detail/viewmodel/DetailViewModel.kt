package woowacourse.shopping.view.detail.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import woowacourse.shopping.data.model.Product
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.util.Event
import woowacourse.shopping.view.cart.listener.QuantityClickListener
import woowacourse.shopping.view.cart.viewmodel.CartViewModel.Companion.DESCENDING_SORT_ORDER
import woowacourse.shopping.view.detail.listener.DetailClickListener
import woowacourse.shopping.view.state.UiState

class DetailViewModel(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
    private val recentProductRepository: RecentProductRepository,
    private val productId: Int,
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

    private val _quantity = MutableLiveData(1)
    val quantity: LiveData<Int>
        get() = _quantity

    val totalPrice: LiveData<Int> =
        quantity.map { quantityValue ->
            product.value?.price?.times(quantityValue) ?: 0
        }

    private val _isMostRecentProductVisible = MutableLiveData<Boolean>()
    val isMostRecentProductVisible: LiveData<Boolean>
        get() = _isMostRecentProductVisible

    private val _navigateToCart = MutableLiveData<Event<Boolean>>()
    val navigateToCart: LiveData<Event<Boolean>>
        get() = _navigateToCart

    private val _navigateToRecentDetail = MutableLiveData<Event<Boolean>>()
    val navigateToRecentDetail: LiveData<Event<Boolean>>
        get() = _navigateToRecentDetail

    private val _isFinishButtonClicked = MutableLiveData<Event<Boolean>>()
    val isFinishButtonClicked: LiveData<Event<Boolean>>
        get() = _isFinishButtonClicked

    init {
        loadProduct()
    }

    fun saveRecentProduct(isMostRecentProductClicked: Boolean) {
        _mostRecentProduct.value = recentProductRepository.findMostRecentProduct()
        val currentProduct = product.value ?: return
        recentProductRepository.save(currentProduct)
        updateRecentProductVisible(isMostRecentProductClicked)
    }

    fun updateRecentProductVisible(isMostRecentProductClicked: Boolean) {
        _isMostRecentProductVisible.value =
            if (mostRecentProduct.value == null || product.value?.productId == mostRecentProduct.value?.productId) {
                false
            } else {
                !isMostRecentProductClicked
            }
    }

    private fun loadProduct() {
        runCatching {
            productRepository.getProductById(productId)
        }.onSuccess { productResult ->
            val product = productResult.getOrNull() ?: return
            _product.value = product
            _detailUiState.value = UiState.Success(product)
        }.onFailure {
            _detailUiState.value = UiState.Error(it)
        }
    }

    private fun saveCartItem() {
        if (detailUiState.value is UiState.Success) {
            runCatching {
                val totalQuantity = cartRepository.getCartTotalQuantity().getOrNull()?.quantity ?: 0
                cartRepository.getCartResponse(0, totalQuantity, DESCENDING_SORT_ORDER)
            }.onSuccess { cartResponse ->
                val cartItems = cartResponse.getOrNull()?.cartItems
                val cartItem = cartItems?.firstOrNull { it.product.productId == productId }
                val currentQuantity = cartItem?.quantity ?: 0
                val quantity = quantity.value ?: 0

                if (cartItem == null) {
                    cartRepository.addCartItem(productId, quantity)
                } else {
                    cartRepository.updateCartItem(cartItem.cartItemId, quantity + currentQuantity)
                }
            }
        }
    }

    override fun onPutCartButtonClick() {
        saveCartItem()
        _navigateToCart.value = Event(true)
    }

    override fun onRecentProductClick() {
        _isMostRecentProductVisible.value = false
        _navigateToRecentDetail.value = Event(true)
    }

    override fun onQuantityPlusButtonClick(productId: Int) {
        _quantity.value = quantity.value?.plus(1)
    }

    override fun onQuantityMinusButtonClick(productId: Int) {
        _quantity.value = (quantity.value?.minus(1))?.coerceAtLeast(1)
    }

    override fun onFinishButtonClick() {
        _isFinishButtonClicked.value = Event(true)
    }
}
