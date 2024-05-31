package woowacourse.shopping.view.detail

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
import woowacourse.shopping.view.cart.CartViewModel.Companion.DESCENDING_SORT_ORDER
import woowacourse.shopping.view.cart.QuantityClickListener
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

    private var _quantity = MutableLiveData(1)
    val quantity: LiveData<Int>
        get() = _quantity

    val totalPrice: LiveData<Int> =
        quantity.map { quantityValue ->
            product.value?.price?.times(quantityValue) ?: 0
        }

    private val totalQuantity: Int =
        cartRepository.getCartTotalQuantity().getOrNull()?.quantity ?: 0

    init {
        loadProduct()
    }

    private fun loadProduct() {
        try {
            val productData = productRepository.getProductById(productId).getOrNull() ?: return
            _product.value = productData
            _detailUiState.value = UiState.Success(productData)
        } catch (e: Exception) {
            _detailUiState.value = UiState.Error(e)
        }
    }

    fun saveCartItem() {
        val state = detailUiState.value
        if (state is UiState.Success) {
            val cartResponse =
                cartRepository.getCartItems(0, totalQuantity, DESCENDING_SORT_ORDER).getOrNull()
            val cartItems = cartResponse?.cartItems
            val cartItemId =
                cartItems?.firstOrNull { it.product.productId == productId }?.cartItemId
            val currentQuantity =
                cartItems?.firstOrNull { it.cartItemId == cartItemId }?.quantity ?: 0
            val quantity = quantity.value ?: 0

            if (cartItemId == null) {
                cartRepository.addCartItem(productId, quantity)
            } else {
                cartRepository.updateCartItem(cartItemId, quantity + currentQuantity)
            }
        }
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
