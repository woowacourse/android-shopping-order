package woowacourse.shopping.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import woowacourse.shopping.data.model.Product2
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.repository.CartRepository.Companion.DEFAULT_QUANTITY
import woowacourse.shopping.domain.repository.CartRepository2
import woowacourse.shopping.domain.repository.ProductRepository2
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.util.Event
import woowacourse.shopping.view.cart.QuantityClickListener
import woowacourse.shopping.view.state.UIState

class DetailViewModel(
    private val cartRepository: CartRepository2,
    private val productRepository: ProductRepository2,
    private val recentProductRepository: RecentProductRepository,
    private val productId: Int,
) : ViewModel(), DetailClickListener, QuantityClickListener {
    private val _detailUiState = MutableLiveData<UIState<Product2>>(UIState.Loading)
    val detailUiState: LiveData<UIState<Product2>>
        get() = _detailUiState

    private val _product: MutableLiveData<Product2> = MutableLiveData()
    val product: LiveData<Product2>
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
            _detailUiState.value = UIState.Success(productData)
        } catch (e: Exception) {
            _detailUiState.value = UIState.Error(e)
        }
    }

    fun saveCartItem() {
        println("inserted quantity : ${quantity.value}")
        val state = detailUiState.value
        if (state is UIState.Success) {
            val cartResponse = cartRepository.getCartItems(0, totalQuantity, "asc").getOrNull()
            val cartItems = cartResponse?.cartItems
            val cartItemId = cartItems?.firstOrNull { it.product.id == productId }?.cartItemId
            val currentQuantity =
                cartItems?.firstOrNull { it.cartItemId == cartItemId }?.quantity ?: 0
            val quantity = quantity.value ?: 0

            if (cartItemId == null) {
                println("cart item is null")
                cartRepository.addCartItem(productId, quantity)
            } else {
                println("cart item $currentQuantity")
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
            if (mostRecentProduct.value == null || product.value?.id == mostRecentProduct.value?.productId) {
                false
            } else {
                !isMostRecentProductClicked
            }
    }

    override fun onPutCartButtonClick() {
        println("currentQuantity : $totalQuantity")
        println("add value : ${quantity.value}")
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
