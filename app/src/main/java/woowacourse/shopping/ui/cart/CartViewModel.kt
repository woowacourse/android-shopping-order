package woowacourse.shopping.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.common.MutableSingleLiveData
import woowacourse.shopping.common.SingleLiveData
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Quantity
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.ui.products.adapter.type.ProductUiModel

class CartViewModel(
    private val productRepository: ProductRepository,
    private val recentProductRepository: RecentProductRepository,
    private val cartRepository: CartRepository,
) : ViewModel(), CartListener {
    private val _cartUiModels = MutableLiveData<CartUiModels>()
    val cartUiModels: LiveData<CartUiModels> get() = _cartUiModels

    private val _isLoadingCart = MutableLiveData<Boolean>()
    val isLoadingCart: LiveData<Boolean> get() = _isLoadingCart

    private val _changedCartEvent = MutableSingleLiveData<Unit>()
    val changedCartEvent: SingleLiveData<Unit> get() = _changedCartEvent

    val totalPrice: LiveData<Int> = _cartUiModels.map { it.selectedTotalPrice() }
    val isEnabledOrder: LiveData<Boolean> = totalPrice.map { it != 0 }

    val totalQuantity: LiveData<Int> = _cartUiModels.map { it.selectedTotalQuantity() }

    private val cartItemSelectedCount: LiveData<Int> = _cartUiModels.map { it.selectedTotalCount() }
    val cartItemAllSelected: LiveData<Boolean> = cartItemSelectedCount.map { it > 0 && it == cartUiModels().size }

    private val _recommendProductUiModels = MutableLiveData<List<ProductUiModel>>()
    val recommendProductUiModels: LiveData<List<ProductUiModel>> get() = _recommendProductUiModels

    private val _orderEvent = MutableSingleLiveData<Unit>()
    val orderEvent: SingleLiveData<Unit> get() = _orderEvent

    private val _visibleAllToggleView = MutableLiveData(true)

    val visibleAllToggleView: LiveData<Boolean> get() = _visibleAllToggleView

    private val _selectedCartItemIds = MutableLiveData<List<Int>>()
    val selectedCartItemIds: LiveData<List<Int>> get() = _selectedCartItemIds

    private val _productsLoadError = MutableSingleLiveData<Throwable>()
    val productsLoadError: SingleLiveData<Throwable> get() = _productsLoadError

    private val _cartItemAddError = MutableSingleLiveData<Throwable>()
    val cartItemAddError: SingleLiveData<Throwable> get() = _cartItemAddError

    private val _cartItemDeleteError = MutableSingleLiveData<Throwable>()
    val cartItemDeleteError: SingleLiveData<Throwable> get() = _cartItemDeleteError

    fun loadAllCartItems() =
        viewModelScope.launch {
            _isLoadingCart.value = true
            cartRepository.findAll()
                .onSuccess { cartItems ->
                    if (cartItems.isEmpty()) {
                        _cartUiModels.value = CartUiModels()
                        return@onSuccess
                    }
                    cartItems.forEach { cartItem -> loadProduct(cartItem) }
                }
                .onFailure {
                    _productsLoadError.setError(it)
                }
            _isLoadingCart.value = false
        }

    private fun loadProduct(cartItem: CartItem) =
        viewModelScope.launch {
            productRepository.find(cartItem.product.id)
                .onSuccess { product ->
                    updateCartUiModels(product, cartItem)
                }.onFailure {
                    _productsLoadError.setError(it)
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

    override fun increaseQuantity(productId: Int) {
        val cartUiModel = cartUiModels().findByProductId(productId)
        if (cartUiModel == null) {
            addCartItem(productId)
            return
        }
        var newQuantity = cartUiModel.quantity
        changeQuantity(cartUiModel, ++newQuantity, _cartItemAddError)
    }

    private fun addCartItem(productId: Int) =
        viewModelScope.launch {
            cartRepository.add(productId)
                .onSuccess {
                    _changedCartEvent.setValue(Unit)
                    loadAllCartItems()
                    if (isRecommendProduct(productId)) {
                        updateRecommendProducts(productId)
                    }
                }
                .onFailure {
                    _cartItemAddError.setError(it)
                }
        }

    override fun decreaseQuantity(productId: Int) {
        val cartUiModel = cartUiModels().findByProductId(productId) ?: return
        if (cartUiModel.quantity.count == 1) {
            deleteCartItem(cartUiModel.cartItemId)
            return
        }
        var newQuantity = cartUiModel.quantity
        changeQuantity(cartUiModel, --newQuantity, _cartItemDeleteError)
    }

    override fun deleteCartItem(cartItemId: Int) {
        viewModelScope.launch {
            val cartUiModel = cartUiModels().find(cartItemId) ?: return@launch
            cartRepository.delete(cartUiModel.cartItemId)
                .onSuccess {
                    _changedCartEvent.setValue(Unit)
                    updateDeletedCart(cartUiModel)
                    if (isRecommendProduct(cartUiModel.productId)) {
                        updateRecommendProducts(cartUiModel.productId, Quantity())
                    }
                }
                .onFailure {
                    _cartItemDeleteError.setError(it)
                }
        }
    }

    private fun updateDeletedCart(deletedCartUiModel: CartUiModel) {
        val newCartUiModels = cartUiModels().remove(deletedCartUiModel)
        _cartUiModels.value = newCartUiModels
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
        errorEvent: MutableSingleLiveData<Throwable>,
    ) = viewModelScope.launch {
        cartRepository.changeQuantity(cartUiModel.cartItemId, quantity)
            .onSuccess {
                _changedCartEvent.setValue(Unit)
                loadProduct(cartUiModel.copy(quantity = quantity).toCartItem())
                if (isRecommendProduct(cartUiModel.productId)) {
                    updateRecommendProducts(cartUiModel.productId, quantity)
                }
            }
            .onFailure {
                errorEvent.setError(it)
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
    }

    fun order() {
        _orderEvent.setValue(Unit)
    }

    fun navigateCartRecommend() {
        _visibleAllToggleView.value = false
    }

    fun navigateCartSelection() {
        _visibleAllToggleView.value = true
    }

    fun loadRecommendProducts() =
        viewModelScope.launch {
            val recentProduct =
                recentProductRepository.findLastOrNull().getOrNull() ?: return@launch
            val recentProductCategory = recentProduct.product.category
            val cartItems = cartUiModels().toCartItems()

            productRepository.findRecommendProducts(recentProductCategory, cartItems)
                .onSuccess { recommendProducts ->
                    _recommendProductUiModels.value =
                        recommendProducts.map { ProductUiModel.from(it) }
                }.onFailure {
                    _productsLoadError.setError(it)
                }
        }

    fun updateSelectedCartItemIds() {
        val cartItemIds = cartUiModels().selectedCartItemIds()
        _selectedCartItemIds.value = cartItemIds
    }

    private fun MutableSingleLiveData<Throwable>.setError(throwable: Throwable) {
        this.setValue(throwable)
    }

    private fun cartUiModels(): CartUiModels {
        return cartUiModels.value ?: CartUiModels()
    }
}
