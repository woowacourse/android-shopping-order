package woowacourse.shopping.presentation.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.example.domain.model.CartItem
import com.example.domain.model.Product
import com.example.domain.model.Quantity
import com.example.domain.repository.RecentProductRepository
import woowacourse.shopping.common.Event
import woowacourse.shopping.data.repository.DefaultCartRepository
import woowacourse.shopping.data.repository.DefaultOrderRepository
import woowacourse.shopping.data.repository.DefaultProductRepository
import woowacourse.shopping.presentation.products.adapter.type.ProductUiModel

class CartViewModel(
    private val productRepository: DefaultProductRepository,
    private val recommendRepository: com.example.domain.repository.RecentProductRepository,
    private val cartRepository: DefaultCartRepository,
    private val orderRepository: DefaultOrderRepository,
) : ViewModel(), CartListener {
    private val _cartUiState = MutableLiveData<Event<CartUiState>>()
    val cartUiState: LiveData<Event<CartUiState>> get() = _cartUiState

    private val _totalPrice = MutableLiveData<Int>()
    val totalPrice: LiveData<Int> get() = _totalPrice

    private val _changedCartEvent = MutableLiveData<Event<Unit>>()
    val changedCartEvent: LiveData<Event<Unit>> get() = _changedCartEvent

    private val _cartItemSelectedCount = MutableLiveData<Int>(0)
    val cartItemSelectedCount: LiveData<Int> get() = _cartItemSelectedCount

    val cartItemAllSelected: LiveData<Boolean> =
        _cartItemSelectedCount.map { it == cartUiModels()?.size }

    private val _recommendProductUiModels = MutableLiveData<List<ProductUiModel>>()
    val recommendProductUiModels: LiveData<List<ProductUiModel>> get() = _recommendProductUiModels

    private val _isSuccessCreateOrder = MutableLiveData<Event<Boolean>>()
    val isSuccessCreateOrder: LiveData<Event<Boolean>> get() = _isSuccessCreateOrder

    private val _navigateEvent = MutableLiveData<Event<Unit>>()
    val navigateEvent: LiveData<Event<Unit>> get() = _navigateEvent

    private val _checkboxVisibility = MutableLiveData<Boolean>(true)
    val checkboxVisibility: LiveData<Boolean> get() = _checkboxVisibility

    private val _orderButtonEnabled = MutableLiveData(false)
    val orderButtonEnabled: LiveData<Boolean> get() = _orderButtonEnabled

    init {
        loadAllCartItems()
    }

    private fun loadAllCartItems() {
        /*
        val totalQuantityCount = cartRepository.syncGetCartQuantityCount()
        cartRepository.getAllCartItem(
            totalQuantityCount,
            object : DataCallback<List<CartItem>> {
                override fun onSuccess(result: List<CartItem>) {
                    result.forEach {
                        loadProduct(it)
                    }
                }

                override fun onFailure(t: Throwable) {
                }
            },
        )
         */
        updateTotalPrice()
    }

    private fun updateTotalPrice() {
        val uiModels = cartUiModels() ?: emptyList()
        val totalPrice =
            uiModels
                .filter { it.isSelected }
                .sumOf { it.totalPrice() }
        _totalPrice.value = totalPrice
        _orderButtonEnabled.value = totalPrice != 0
    }

    private fun loadProduct(cartItem: com.example.domain.model.CartItem) {
        /*
        productRepository.find(
            cartItem.productId,
            object : DataCallback<Product> {
                override fun onSuccess(result: Product) {
                    updateCartUiState(result, cartItem)
                    updateTotalPrice()
                }

                override fun onFailure(t: Throwable) {
                }
            },
        )

         */
    }

    @Synchronized
    private fun updateCartUiState(
        product: com.example.domain.model.Product,
        cartItem: com.example.domain.model.CartItem,
    ) {
        val oldCartUiModels = cartUiModels() ?: emptyList()
        val oldCartUiModel = cartUiModel(product.id) ?: CartUiModel.from(product, cartItem)
        val newCartUiModel = oldCartUiModel.copy(quantity = cartItem.quantity)
        val newCartUiModels = oldCartUiModels.upsert(newCartUiModel).sortedBy { it.cartItemId }
        val newCartUiState = Event(CartUiState.Success(newCartUiModels))
        _cartUiState.value = newCartUiState
    }

    private fun List<CartUiModel>.upsert(cartUiModel: CartUiModel): List<CartUiModel> {
        val list = toMutableList()
        if (this.none { it.cartItemId == cartUiModel.cartItemId }) {
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
        val cartUiModel = cartUiModel(productId) ?: return
        /*
        cartRepository.deleteCartItem(
            cartUiModel.cartItemId,
            object : DataCallback<Unit> {
                override fun onSuccess(result: Unit) {
                    updateDeletedCart()
                }

                override fun onFailure(t: Throwable) {
                    setError()
                }
            },
        )
         */
    }

    private fun updateDeletedCart() {
        _cartUiState.value = Event(CartUiState.Success(listOf()))
        loadAllCartItems()
    }

    override fun increaseQuantity(productId: Int) {
        _changedCartEvent.value = Event(Unit)

        val cartUiModel = cartUiModel(productId)
        if (cartUiModel == null) {
            addCartItem(productId)
            return
        }

        var newQuantity = cartUiModel.quantity
        setQuantity(cartUiModel.cartItemId, ++newQuantity)
    }

    override fun decreaseQuantity(productId: Int) {
        _changedCartEvent.value = Event(Unit)

        val cartUiModel = cartUiModel(productId) ?: return
        if (cartUiModel.quantity.count == 1) {
            deleteCartItem(productId)
            return
        }

        var newQuantity = cartUiModel.quantity
        setQuantity(cartUiModel.cartItemId, --newQuantity)
    }

    override fun selectCartItem(
        productId: Int,
        isSelected: Boolean,
    ) {
        val oldCartUiModels = cartUiModels() ?: emptyList()
        val oldCartUiModel = cartUiModel(productId) ?: return
        if (oldCartUiModel.isSelected == isSelected) return

        val newCartUiModels = oldCartUiModels.upsert(oldCartUiModel.copy(isSelected = isSelected))
        val newCartUiState = Event(CartUiState.Success(newCartUiModels))
        _cartUiState.value = newCartUiState
        updateTotalPrice()
        updateCartSelectedCount(isSelected)
    }

    private fun updateCartSelectedCount(isSelected: Boolean) {
        val cartItemSelectedCount = _cartItemSelectedCount.value ?: 0
        val newCartItemSelectedCount =
            if (isSelected) cartItemSelectedCount + 1 else cartItemSelectedCount - 1
        this._cartItemSelectedCount.value = newCartItemSelectedCount
    }

    private fun setQuantity(
        cartItemId: Int,
        quantity: com.example.domain.model.Quantity,
    ) {
        cartRepository.setCartItemQuantity(
            cartItemId,
            quantity,
            object : DataCallback<Unit> {
                override fun onSuccess(result: Unit) {
                    loadAllCartItems()
                }

                override fun onFailure(t: Throwable) {
                    setError()
                }
            },
        )
    }

    override fun selectAllCartItem(isChecked: Boolean) {
        if (cartItemAllSelected.value == true && isChecked) return
        cartUiModels()?.forEach {
            selectCartItem(it.productId, isSelected = isChecked)
        }
    }

    override fun navigateCartRecommend() {
        _navigateEvent.postValue(Event(Unit))
        _checkboxVisibility.value = false
    }

    fun loadRecommendProductUiModels() {
        val cartItems = cartUiModels()?.map { it.toCartItem() } ?: return
        val recommendProducts = recommendRepository.getRecommendProducts(cartItems = cartItems)
        _recommendProductUiModels.value = recommendProducts.map { ProductUiModel.from(it) }
    }

    private fun addCartItem(productId: Int) {
        cartRepository.addCartItem(
            productId = productId,
            dataCallback =
                object : DataCallback<Unit> {
                    override fun onSuccess(result: Unit) {
                        loadAllCartItems()
                    }

                    override fun onFailure(t: Throwable) {
                        setError()
                    }
                },
        )
    }

    fun createOrder() {
        val cartUiModels = cartUiModels() ?: return
        val cartItemIds = cartUiModels.filter { it.isSelected }.map { it.cartItemId }
        orderRepository.createOrder(
            cartItemIds,
            object : DataCallback<Unit> {
                override fun onSuccess(result: Unit) {
                    _isSuccessCreateOrder.value = Event(true)
                    deleteCartItemIds(cartItemIds)
                }

                override fun onFailure(t: Throwable) {
                    _isSuccessCreateOrder.value = Event(false)
                }
            },
        )
    }

    private fun deleteCartItemIds(cartItemIds: List<Int>) {
        _changedCartEvent.value = Event(Unit)
        cartItemIds.forEach {
            cartRepository.deleteCartItem(
                it,
                object : DataCallback<Unit> {
                    override fun onSuccess(result: Unit) {}

                    override fun onFailure(t: Throwable) {
                        setError()
                    }
                },
            )
        }
    }

    private fun CartUiModel.toCartItem() = com.example.domain.model.CartItem(cartItemId, productId, quantity)

    private fun setError() {
        _cartUiState.value = Event(CartUiState.Failure)
    }

    private fun cartUiModel(productId: Int): CartUiModel? {
        return cartUiModels()?.find { it.productId == productId }
    }

    private fun cartUiModels(): List<CartUiModel>? {
        val cartUiState = cartUiState.value?.peekContent()
        if (cartUiState is CartUiState.Success) {
            return cartUiState.cartUiModels
        }
        return null
    }
}