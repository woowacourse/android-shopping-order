package woowacourse.shopping.presentation.ui.cart.selection

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.database.OrderDatabase
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.domain.repository.ShoppingItemsRepository
import woowacourse.shopping.presentation.event.Event
import woowacourse.shopping.presentation.event.SingleLiveEvent
import woowacourse.shopping.presentation.state.UIState
import woowacourse.shopping.presentation.ui.cart.CartItemUiModel

class SelectionViewModel(
    private val shoppingRepository: ShoppingItemsRepository,
    private val recentProductRepository: RecentProductRepository,
    private val cartRepository: CartRepository,
) : ViewModel(),
    CartItemSelectionEventHandler,
    SelectionCountHandler {
    private var cartItems: List<CartItem> = emptyList()

    private val _uiCartItemsState: MutableLiveData<UIState<List<CartItemUiModel>>> =
        MutableLiveData()
    val uiCartItemsState: LiveData<UIState<List<CartItemUiModel>>>
        get() = _uiCartItemsState

    private val _isEmpty = MediatorLiveData(false)
    val isEmpty: LiveData<Boolean>
        get() = _isEmpty

    private val _order = MutableLiveData(OrderDatabase.getOrder())
    val order: LiveData<Order>
        get() = _order

    private val _isAllSelected: MediatorLiveData<Boolean> = MediatorLiveData(false)
    val isAllSelected: LiveData<Boolean>
        get() = _isAllSelected

    private val _isOrderEmpty = MediatorLiveData(true)
    val isOrderEmpty: LiveData<Boolean>
        get() = _isOrderEmpty

    private val _navigateToDetail = MutableLiveData<Event<Long>>()
    val navigateToDetail: LiveData<Event<Long>>
        get() = _navigateToDetail

    private val _totalOrderPrice = MutableLiveData<Long>(order.value?.getTotalPrice() ?: 0L)
    val totalOrderPrice: LiveData<Long>
        get() = _totalOrderPrice

    private val _totalOrderQuantity = MutableLiveData<Int>(order.value?.getTotalQuantity() ?: 0)
    val totalOrderQuantity: LiveData<Int>
        get() = _totalOrderQuantity

    private val _isLoading = MutableLiveData(Event(false))
    val isLoading: LiveData<Event<Boolean>>
        get() = _isLoading

    private val _quantityChangedIds: SingleLiveEvent<Set<Long>> = SingleLiveEvent()
    val quantityChangedIds: LiveData<Set<Long>>
        get() = _quantityChangedIds

    private val _isCheckedChangedIds: SingleLiveEvent<Set<Long>> = SingleLiveEvent()
    val isCheckedChangedIds: LiveData<Set<Long>>
        get() = _isCheckedChangedIds

    private val _deletedId = MutableLiveData<Event<Long>>()
    val deletedId: LiveData<Event<Long>>
        get() = _deletedId

    init {
        with(_isAllSelected) {
            addSource(order) { checkAllSelected(uiCartItemsState, order) }
            addSource(uiCartItemsState) { checkAllSelected(uiCartItemsState, order) }
        }
        with(_isEmpty) {
            addSource(uiCartItemsState) { checkCartIsEmpty(uiCartItemsState) }
        }
        with(_isOrderEmpty) {
            addSource(order) { this.value = it.map.isEmpty() }
        }
    }

    fun setLoadingState(value: Boolean) {
        _isLoading.value = Event(value)
    }

    fun setUpCartItems() {
        cartRepository.fetchCartItemsInfo() { result ->
            result.onSuccess { items ->
                cartItems = items
                setUpUIState()
            }.onFailure {
                _uiCartItemsState.value = UIState.Error(it)
                setLoadingState(false)
                Log.d(this::class.java.simpleName, "$it")
            }
        }
    }

    private fun setUpUIState() {
        if (_uiCartItemsState.value !is UIState.Error) {
            _uiCartItemsState.value = if (cartItems.isEmpty()) {
                UIState.Empty
            } else {
                val prevOrder = OrderDatabase.getOrder()
                UIState.Success(cartItems.map {
                    it.toUiModel(
                        prevOrder.containsCartItem(it)
                    )
                })
            }
        }
        updatePriceAndQuantity()
        setLoadingState(false)
    }

    private fun checkAllSelected(
        uiState: LiveData<UIState<List<CartItemUiModel>>>,
        order: LiveData<Order>,
    ) {
        when (uiState.value) {
            is UIState.Success -> {
                val cartSize = (uiState.value as UIState.Success<List<CartItemUiModel>>).data.size
                _isAllSelected.value = (cartSize > 0) && (order.value?.map?.size == cartSize)
            }

            else -> {
                _isAllSelected.value = false
            }
        }
    }

    private fun checkCartIsEmpty(uiState: LiveData<UIState<List<CartItemUiModel>>>) {
        if (uiState.value is UIState.Success<List<CartItemUiModel>>) {
            _isEmpty.value =
                (uiState.value as UIState.Success<List<CartItemUiModel>>).data.isEmpty()
        }
    }

    override fun onCheckItem(cartItemId: Long) {
        val cartItem = cartItems.find { it.id == cartItemId }
        if (cartItem != null) {
            if (order.value?.containsCartItem(cartItem) == true) {
                removeFromOrder(cartItem.id)
            } else {
                addToOrder(cartItem)
            }
            updatePriceAndQuantity()
        }
    }

    private fun removeFromOrder(cartItemId: Long) {
        val prevOrder = order.value ?: Order()
        prevOrder.removeCartItem(cartItemId)
        _order.value = prevOrder
        _uiCartItemsState.value = UIState.Success(getUpdatedCheckedList(cartItemId, false))
        _isCheckedChangedIds.value = setOf(cartItemId)
    }

    private fun addToOrder(cartItem: CartItem) {
        val prevOrder = order.value ?: Order()
        prevOrder.addCartItem(cartItem)
        _order.value = prevOrder
        _uiCartItemsState.value = UIState.Success(getUpdatedCheckedList(cartItem.id, true))
        _isCheckedChangedIds.value = setOf(cartItem.id)
    }

    private fun getUpdatedCheckedList(
        cartItemId: Long,
        isChecked: Boolean,
    ): List<CartItemUiModel> {
        val uiCartItems = (uiCartItemsState.value as UIState.Success<List<CartItemUiModel>>).data
        return uiCartItems.map {
            if (it.id == cartItemId) {
                it.copy(isChecked = isChecked)
            } else {
                it
            }
        }
    }

    private fun updatePriceAndQuantity() {
        _totalOrderPrice.postValue(order.value?.getTotalPrice() ?: 0L)
        _totalOrderQuantity.postValue(order.value?.getTotalQuantity() ?: 0)
    }

    fun selectAllByCondition() {
        val prevOrder = order.value ?: Order()
        val uiCartItems = (uiCartItemsState.value as UIState.Success<List<CartItemUiModel>>).data
        if (isAllSelected.value == true) {
            unSelectAll(prevOrder, uiCartItems)
        } else {
            selectAll(prevOrder, uiCartItems)
        }
        _isCheckedChangedIds.value = uiCartItems.map { it.id }.toSet()
        updatePriceAndQuantity()
    }

    private fun unSelectAll(
        prevOrder: Order,
        uiCartItems: List<CartItemUiModel>,
    ) {
        prevOrder.clearOrder()
        _order.value = prevOrder
        _uiCartItemsState.value = UIState.Success(uiCartItems.map { it.copy(isChecked = false) })
    }

    private fun selectAll(
        prevOrder: Order,
        uiCartItems: List<CartItemUiModel>,
    ) {
        prevOrder.selectAllCartItems(cartItems)
        _order.value = prevOrder
        _uiCartItemsState.value = UIState.Success(uiCartItems.map { it.copy(isChecked = true) })
    }

    override fun onProductClicked(productId: Long) {
        _navigateToDetail.postValue(Event(productId))
    }

    override fun onXButtonClicked(itemId: Long) {
        removeFromOrder(itemId)
        deleteItem(itemId)
    }

    private fun deleteItem(itemId: Long) {
        cartRepository.deleteCartItem(itemId) { result ->
            result.onSuccess {
                _deletedId.postValue(Event(itemId))
                updatePriceAndQuantity()
                cartItems = cartItems.filterNot { it.id == itemId }
                val cartItems =
                    (uiCartItemsState.value as UIState.Success<List<CartItemUiModel>>).data
                val updatedCartItems = cartItems.filterNot { it.id == itemId }
                _uiCartItemsState.postValue(UIState.Success(updatedCartItems))
            }.onFailure {
                Log.d(this::class.java.simpleName, "$it")
            }
        }
    }

    override fun increaseCount(
        productId: Long,
        quantity: Int,
    ) {
        cartRepository.updateCartItemQuantityWithProductId(productId, quantity.inc()) { result ->
            result.onSuccess {
                changeCartItemQuantity(productId, quantity.inc())
                changeUiCartItemQuantity(productId, quantity.inc())
                updatePriceAndQuantity()
            }.onFailure {
                Log.d(this::class.java.simpleName, "$it")
            }
        }
    }

    override fun decreaseCount(
        productId: Long,
        quantity: Int,
    ) {
        if (quantity > 1) {
            cartRepository.updateCartItemQuantityWithProductId(
                productId,
                quantity.dec(),
            ) { result ->
                result.onSuccess {
                    changeCartItemQuantity(productId, quantity.dec())
                    changeUiCartItemQuantity(productId, quantity.dec())
                    updatePriceAndQuantity()
                }.onFailure {
                    Log.d(this::class.java.simpleName, "$it")
                }
            }
        }
    }

    private fun changeCartItemQuantity(
        productId: Long,
        newQuantity: Int,
    ) {
        cartItems = cartItems.map { item ->
            if (item.productId == productId) {
                addToOrder(item.copy(quantity = newQuantity))
                item.copy(quantity = newQuantity)
            } else {
                item
            }
        }
    }

    private fun changeUiCartItemQuantity(
        productId: Long,
        newQuantity: Int,
    ) {
        val uiCartItems =
            (uiCartItemsState.value as UIState.Success<List<CartItemUiModel>>).data.map {
                if (it.productId == productId) {
                    it.copy(quantity = newQuantity)
                } else {
                    it
                }
            }
        _uiCartItemsState.postValue(UIState.Success(uiCartItems))
        _quantityChangedIds.postValue(setOf(productId))
    }

    private fun CartItem.toUiModel(isChecked: Boolean): CartItemUiModel {
        return CartItemUiModel(
            id = this.id,
            productId = this.productId,
            productName = this.productName,
            price = this.price,
            quantity = this.quantity,
            imgUrl = this.imgUrl,
            isChecked = isChecked,
        )
    }
}
