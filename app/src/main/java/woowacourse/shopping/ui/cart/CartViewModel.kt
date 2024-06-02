package woowacourse.shopping.ui.cart

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
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
    private val recentRepository: RecentProductRepository,
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
) : ViewModel(), CartListener {
    private val _cartUiModels = MutableLiveData<List<CartUiModel>>()
    val cartUiModels: LiveData<List<CartUiModel>> get() = _cartUiModels

    private val _cartLoadingEvent = MutableLiveData<Event<Unit>>()
    val cartLoadingEvent: LiveData<Event<Unit>> get() = _cartLoadingEvent

    private val _cartErrorEvent = MutableLiveData<Event<Unit>>()
    val cartErrorEvent: LiveData<Event<Unit>> get() = _cartErrorEvent

    private val _totalPrice = MutableLiveData<Int>()
    val totalPrice: LiveData<Int> get() = _totalPrice

    val isEnabledOrder: LiveData<Boolean> = _totalPrice.map { it != 0 }

    private val _changedCartEvent = MutableLiveData<Event<Unit>>()
    val changedCartEvent: LiveData<Event<Unit>> get() = _changedCartEvent

    private val cartItemSelectedCount = MutableLiveData<Int>(0)
    val cartItemAllSelected: LiveData<Boolean> =
        cartItemSelectedCount.map { it > 0 && it == findCartUiModelsOrNull()?.size }

    private val _recommendProductUiModels = MutableLiveData<List<ProductUiModel>>()
    val recommendProductUiModels: LiveData<List<ProductUiModel>> get() = _recommendProductUiModels

    private val _isSuccessCreateOrder = MutableLiveData<Event<Boolean>>()
    val isSuccessCreateOrder: LiveData<Event<Boolean>> get() = _isSuccessCreateOrder

    private val _navigateEvent = MutableLiveData<Event<Unit>>()
    val navigateEvent: LiveData<Event<Unit>> get() = _navigateEvent

    private val _visibleAllToggleView = MutableLiveData(true)
    val visibleAllToggleView: LiveData<Boolean> get() = _visibleAllToggleView

    private val _totalQuantity = MutableLiveData<Int>()
    val totalQuantity: LiveData<Int> get() = _totalQuantity

    init {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({ loadAllCartItems() }, 500)
    }

    private fun loadAllCartItems() {
        _cartLoadingEvent.value = Event(Unit)
        cartRepository.findAll {
            it.onSuccess { cartItems ->
                if (cartItems.isEmpty()) {
                    updateTotalQuantity()
                    updateTotalPrice()
                    return@onSuccess
                }
                cartItems.forEach { cartItem -> loadProduct(cartItem) }
            }.onFailure {
                setError()
            }
        }
    }

    private fun updateTotalQuantity() {
        val cartUiModels = findCartUiModelsOrNull() ?: emptyList()
        val totalQuantity =
            cartUiModels
                .filter { it.isSelected }
                .sumOf { it.quantity.count }
        _totalQuantity.value = totalQuantity
    }

    private fun updateTotalPrice() {
        val uiModels = findCartUiModelsOrNull() ?: emptyList()
        val totalPrice =
            uiModels
                .filter { it.isSelected }
                .sumOf { it.totalPrice() }
        _totalPrice.value = totalPrice
    }

    private fun loadProduct(cartItem: CartItem) {
        productRepository.find(cartItem.productId) {
            it.onSuccess { product ->
                updateCart(product, cartItem)
            }.onFailure {
                setError()
            }
        }
    }

    private fun updateCart(
        updatedProduct: Product,
        updatedCartItem: CartItem,
    ) {
        updateCartUiModels(updatedProduct, updatedCartItem)
        updateTotalQuantity()
        updateTotalPrice()
    }

    private fun updateCartUiModels(
        product: Product,
        cartItem: CartItem,
    ) {
        val oldCartUiModels = findCartUiModelsOrNull() ?: emptyList()
        val oldCartUiModel =
            findCartUiModelByProductId(product.id) ?: CartUiModel.from(product, cartItem)

        var newCartUiModel = oldCartUiModel.copy(quantity = cartItem.quantity)

        if (isRecommendProduct(product.id)) {
            newCartUiModel = newCartUiModel.copy(isSelected = true)
        }

        val newCartUiModels = oldCartUiModels.upsert(newCartUiModel).sortedBy { it.cartItemId }
        _cartUiModels.value = newCartUiModels
    }

    private fun List<CartUiModel>.upsert(cartUiModel: CartUiModel): List<CartUiModel> {
        val list = toMutableList()
        if (none { it.cartItemId == cartUiModel.cartItemId }) {
            list += cartUiModel
        } else {
            this.forEachIndexed { index, listItem ->
                if (listItem.cartItemId == cartUiModel.cartItemId) {
                    list[index] = cartUiModel
                }
            }
        }
        return list.toList()
    }

    override fun deleteCartItem(productId: Int) {
        _changedCartEvent.value = Event(Unit)
        val cartUiModel = findCartUiModelByProductId(productId) ?: return
        cartRepository.delete(cartUiModel.cartItemId) {
            it.onSuccess {
                updateDeletedCart(cartUiModel)
                if (isRecommendProduct(cartUiModel.productId)) {
                    updateRecommendProducts(cartUiModel.productId, Quantity())
                }
            }.onFailure {
                setError()
            }
        }
    }

    private fun updateDeletedCart(deletedCartUiModel: CartUiModel) {
        val newCartUiModels = findCartUiModelsOrNull()?.toMutableList() ?: return
        newCartUiModels.remove(deletedCartUiModel)
        _cartUiModels.value = newCartUiModels
        loadAllCartItems()
        updateCartSelectedCount()
    }

    override fun increaseQuantity(productId: Int) {
        _changedCartEvent.value = Event(Unit)

        val cartUiModel = findCartUiModelByProductId(productId)
        if (cartUiModel == null) {
            addCartItem(productId)
            return
        }
        var newQuantity = cartUiModel.quantity
        changeQuantity(cartUiModel, ++newQuantity)
    }

    override fun decreaseQuantity(productId: Int) {
        _changedCartEvent.value = Event(Unit)

        val cartUiModel = findCartUiModelByProductId(productId) ?: return
        if (cartUiModel.quantity.count == 1) {
            deleteCartItem(productId)
            return
        }
        var newQuantity = cartUiModel.quantity
        changeQuantity(cartUiModel, --newQuantity)
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

    override fun selectCartItem(
        productId: Int,
        isSelected: Boolean,
    ) {
        val oldCartUiModels = findCartUiModelsOrNull() ?: emptyList()
        val oldCartUiModel = findCartUiModelByProductId(productId) ?: return
        if (oldCartUiModel.isSelected == isSelected) return

        val newCartUiModels = oldCartUiModels.upsert(oldCartUiModel.copy(isSelected = isSelected))
        _cartUiModels.value = newCartUiModels

        updateTotalPrice()
        updateTotalQuantity()
        updateCartSelectedCount()
    }

    private fun updateCartSelectedCount() {
        val uiModels = findCartUiModelsOrNull() ?: emptyList()
        val cartSelectedCount = uiModels.count { it.isSelected }
        this.cartItemSelectedCount.value = cartSelectedCount
    }

    private fun changeQuantity(
        cartUiModel: CartUiModel,
        quantity: Quantity,
    ) {
        cartRepository.changeQuantity(cartUiModel.cartItemId, quantity) {
            it.onSuccess {
                loadAllCartItems()

                if (isRecommendProduct(cartUiModel.productId)) {
                    updateRecommendProducts(cartUiModel.productId, quantity)
                }
            }.onFailure {
                setError()
            }
        }
    }

    override fun toggleAllCartItem(isSelected: Boolean) {
        if (cartItemAllSelected.value == true && isSelected) return
        findCartUiModelsOrNull()?.forEach {
            selectCartItem(it.productId, isSelected = isSelected)
        }
    }

    fun navigateCartRecommend() {
        _navigateEvent.postValue(Event(Unit))
        _visibleAllToggleView.value = false
    }

    fun loadRecommendProducts() {
        val recentProductCategory = recentRepository.findLastOrNull()?.product?.category ?: return
        val cartItems = findCartUiModelsOrNull()?.map { it.toCartItem() } ?: return

        productRepository.findRecommendProducts(recentProductCategory, cartItems) {
            it.onSuccess { recommendProducts ->
                _recommendProductUiModels.value = recommendProducts.map { ProductUiModel.from(it) }
            }.onFailure {
                setError()
            }
        }
    }

    private fun addCartItem(productId: Int) {
        cartRepository.add(productId) {
            it.onSuccess {
                loadAllCartItems()

                if (isRecommendProduct(productId)) {
                    updateRecommendProducts(productId)
                }
            }.onFailure {
                setError()
            }
        }
    }

    fun createOrder() {
        val cartUiModels = findCartUiModelsOrNull() ?: return
        val cartItemIds = cartUiModels.filter { it.isSelected }.map { it.cartItemId }
        orderRepository.createOrder(cartItemIds) {
            it.onSuccess {
                _isSuccessCreateOrder.value = Event(true)
                deleteCartItems(cartItemIds)
            }.onFailure {
                _isSuccessCreateOrder.value = Event(false)
            }
        }
    }

    private fun deleteCartItems(cartItemIds: List<Int>) {
        _changedCartEvent.value = Event(Unit)
        cartItemIds.forEach { cartItemId ->
            cartRepository.delete(cartItemId) {
                it.onSuccess { }
                    .onFailure { setError() }
            }
        }
    }

    private fun CartUiModel.toCartItem() = CartItem(cartItemId, productId, quantity)

    private fun setError() {
        _cartErrorEvent.value = Event(Unit)
    }

    private fun findCartUiModelByProductId(productId: Int): CartUiModel? {
        return findCartUiModelsOrNull()?.find { it.productId == productId }
    }

    private fun findCartUiModelsOrNull(): List<CartUiModel>? {
        return cartUiModels.value
    }
}
