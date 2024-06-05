package woowacourse.shopping.ui.cart

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.common.Event
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Quantity
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.ui.products.adapter.type.ProductUiModel

class CartViewModel(
    private val productRepository: ProductRepository,
    private val recentProductRepository: RecentProductRepository,
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
) : ViewModel(), CartListener {
    private val _cartUiModels = MutableLiveData<CartUiModels>()
    val cartUiModels: LiveData<CartUiModels> get() = _cartUiModels

    private val _isLoadingCart = MutableLiveData<Boolean>()
    val isLoadingCart: LiveData<Boolean> get() = _isLoadingCart

    private val _cartErrorEvent = MutableLiveData<Event<Unit>>()
    val cartErrorEvent: LiveData<Event<Unit>> get() = _cartErrorEvent

    private val _totalPrice = MutableLiveData<Int>(0)
    val totalPrice: LiveData<Int> get() = _totalPrice

    val isEnabledOrder: LiveData<Boolean> = _totalPrice.map { it != 0 }

    private val _changedCartEvent = MutableLiveData<Event<Unit>>()
    val changedCartEvent: LiveData<Event<Unit>> get() = _changedCartEvent

    private val cartItemSelectedCount = MutableLiveData<Int>(0)
    val cartItemAllSelected: LiveData<Boolean> =
        cartItemSelectedCount.map { it > 0 && it == cartUiModels().size }

    private val _recommendProductUiModels = MutableLiveData<List<ProductUiModel>>()
    val recommendProductUiModels: LiveData<List<ProductUiModel>> get() = _recommendProductUiModels

    private val _isSuccessCreateOrder = MutableLiveData<Event<Boolean>>()
    val isSuccessCreateOrder: LiveData<Event<Boolean>> get() = _isSuccessCreateOrder

    private val _orderEvent = MutableLiveData<Event<Unit>>()
    val orderEvent: LiveData<Event<Unit>> get() = _orderEvent

    private val _visibleAllToggleView = MutableLiveData(true)
    val visibleAllToggleView: LiveData<Boolean> get() = _visibleAllToggleView

    private val _totalQuantity = MutableLiveData<Int>(0)
    val totalQuantity: LiveData<Int> get() = _totalQuantity

    fun loadAllCartItems() =
        viewModelScope.launch {
            _isLoadingCart.value = true
            cartRepository.findAll()
                .onSuccess { cartItems ->
                    _isLoadingCart.value = false
                    if (cartItems.isEmpty()) {
                        _cartUiModels.value = CartUiModels()
                        return@onSuccess
                    }
                    cartItems.forEach { cartItem -> loadProduct(cartItem) }
                }
                .onFailure {
                    setError()
                }
            _isLoadingCart.value = false
        }

    private fun loadProduct(cartItem: CartItem) {
        productRepository.find(cartItem.productId) {
            it.onSuccess { product ->
                updateCartUiModels(product, cartItem)
                updateCart()
            }.onFailure {
                setError()
            }
        }
    }

    private fun updateCartUiModels(
        product: Product,
        cartItem: CartItem,
    ) {
        val oldCartUiModels = cartUiModels()
        var newCartUiModel = oldCartUiModels.newCartUiModelQuantity(product, cartItem)

        if (isRecommendProduct(product.id)) {
            newCartUiModel = newCartUiModel.copy(isSelected = true)
        }

        val newCartUiModels = oldCartUiModels.upsert(newCartUiModel)
        _cartUiModels.value = newCartUiModels
    }

    private fun updateCart() {
        updateTotalQuantity()
        updateTotalPrice()
        updateCartSelectedCount()
    }

    private fun updateCartSelectedCount() {
        val cartSelectedCount = cartUiModels().selectedTotalCount()
        this.cartItemSelectedCount.value = cartSelectedCount
    }

    private fun updateTotalQuantity() {
        val cartUiModels = cartUiModels()
        val totalQuantity = cartUiModels.selectedTotalQuantity()
        _totalQuantity.value = totalQuantity
    }

    private fun updateTotalPrice() {
        val uiModels = cartUiModels()
        val totalPrice = uiModels.selectedTotalPrice()
        _totalPrice.value = totalPrice
    }

    override fun increaseQuantity(productId: Int) {
        _changedCartEvent.value = Event(Unit)
        val cartUiModel = cartUiModels().findByProductId(productId)
        if (cartUiModel == null) {
            addCartItem(productId)
            return
        }
        var newQuantity = cartUiModel.quantity
        changeQuantity(cartUiModel, ++newQuantity)
    }

    private fun addCartItem(productId: Int) =
        viewModelScope.launch {
            cartRepository.add(productId)
                .onSuccess {
                    loadAllCartItems()
                    if (isRecommendProduct(productId)) {
                        updateRecommendProducts(productId)
                    }
                }
                .onFailure {
                    setError()
                }
        }

    override fun decreaseQuantity(productId: Int) {
        _changedCartEvent.value = Event(Unit)

        val cartUiModel = cartUiModels().findByProductId(productId) ?: return
        if (cartUiModel.quantity.count == 1) {
            deleteCartItem(cartUiModel.cartItemId)
            return
        }
        var newQuantity = cartUiModel.quantity
        changeQuantity(cartUiModel, --newQuantity)
    }

    override fun deleteCartItem(cartItemId: Int) {
        viewModelScope.launch {
            _changedCartEvent.value = Event(Unit)
            val cartUiModel = cartUiModels().find(cartItemId) ?: return@launch

            cartRepository.delete(cartUiModel.cartItemId)
                .onSuccess {
                    Log.e("TEST", "삭제성공")
                    updateDeletedCart(cartUiModel)
                    if (isRecommendProduct(cartUiModel.productId)) {
                        updateRecommendProducts(cartUiModel.productId, Quantity())
                    }
                }
                .onFailure {
                    Log.e("TEST", "삭제실패")
                    Log.e("TEST", "${it.localizedMessage}")

                    setError()
                }
        }
    }

    private fun updateDeletedCart(deletedCartUiModel: CartUiModel) {
        val newCartUiModels = cartUiModels().remove(deletedCartUiModel)
        _cartUiModels.value = newCartUiModels
        updateCart()
    }

    private fun isRecommendProduct(productId: Int): Boolean {
        val recommendProductUiModels = _recommendProductUiModels.value ?: return false
        return recommendProductUiModels.any { it.productId == productId }
    }

    private fun updateRecommendProducts(
        productId: Int,
        quantity: Quantity = Quantity(1),
    ) {
        val recommendProductUiModels = _recommendProductUiModels.value?.toMutableList() ?: return
        val recommendProductUiModel =
            recommendProductUiModels.find { it.productId == productId } ?: return
        val position = recommendProductUiModels.indexOf(recommendProductUiModel)

        recommendProductUiModels[position] = recommendProductUiModel.copy(quantity = quantity)
        _recommendProductUiModels.value = recommendProductUiModels
    }

    private fun changeQuantity(
        cartUiModel: CartUiModel,
        quantity: Quantity,
    ) = viewModelScope.launch {
        cartRepository.changeQuantity(cartUiModel.cartItemId, quantity)
            .onSuccess {
                loadProduct(cartUiModel.copy(quantity = quantity).toCartItem())
                if (isRecommendProduct(cartUiModel.productId)) {
                    updateRecommendProducts(cartUiModel.productId, quantity)
                }
            }
            .onFailure {
                setError()
            }
    }

    override fun toggleAllCartItem(isSelected: Boolean) {
        if (cartItemAllSelected.value == true && isSelected) return
        cartUiModels().uiModels.forEach {
            selectCartItem(it.productId, isSelected = isSelected)
        }
    }

    override fun selectCartItem(
        productId: Int,
        isSelected: Boolean,
    ) {
        val newCartUiModels = cartUiModels().select(productId, isSelected)
        _cartUiModels.value = newCartUiModels
        updateCart()
    }

    fun order() {
        _orderEvent.value = Event(Unit)
    }

    fun navigateCartRecommend() {
        _visibleAllToggleView.value = false
    }

    fun navigateCartSelection() {
        _visibleAllToggleView.value = true
    }

    fun loadRecommendProducts() {
        val recentProductCategory =
            recentProductRepository.findLastOrNull()?.product?.category ?: return
        val cartItems = cartUiModels().toCartItems()

        productRepository.findRecommendProducts(recentProductCategory, cartItems) {
            it.onSuccess { recommendProducts ->
                _recommendProductUiModels.value = recommendProducts.map { ProductUiModel.from(it) }
            }.onFailure {
                setError()
            }
        }
    }

    fun createOrder() {
        val cartItemIds = cartUiModels().selectedCartItemIds()
        orderRepository.createOrder(cartItemIds) {
            it.onSuccess {
                _isSuccessCreateOrder.value = Event(true)
                deleteCartItems(cartItemIds)
            }.onFailure {
                _isSuccessCreateOrder.value = Event(false)
            }
        }
    }

    private fun deleteCartItems(cartItemIds: List<Int>) =
        viewModelScope.launch {
            _changedCartEvent.value = Event(Unit)
            cartItemIds.forEach { cartItemId ->
                cartRepository.delete(cartItemId)
                    .onSuccess { deleteCartItem(cartItemId) }
                    .onFailure { setError() }
            }
        }

    private fun setError() {
        _cartErrorEvent.value = Event(Unit)
    }

    private fun cartUiModels(): CartUiModels {
        return cartUiModels.value ?: CartUiModels()
    }
}
