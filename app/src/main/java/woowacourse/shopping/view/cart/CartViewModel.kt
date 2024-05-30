package woowacourse.shopping.view.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import woowacourse.shopping.data.model.CartItem2
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.CartRepository2
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.util.Event
import woowacourse.shopping.view.state.UIState

class CartViewModel(
    private val cartRepository: CartRepository2,
    private val orderRepository: OrderRepository,
) :
    ViewModel(),
    CartItemClickListener,
    QuantityClickListener {

    private val _cartUiState = MutableLiveData<UIState<List<CartItem2>>>(UIState.Loading)
    val cartUiState: LiveData<UIState<List<CartItem2>>>
        get() = _cartUiState

    val isEmpty: LiveData<Boolean> =
        cartUiState.map { state ->
            when (state) {
                is UIState.Success -> {
                    state.data.isEmpty()
                }

                else -> {
                    true
                }
            }
        }

    private val _navigateToDetail = MutableLiveData<Event<Int>>()
    val navigateToDetail: LiveData<Event<Int>>
        get() = _navigateToDetail

    private val _notifyDeletion = MutableLiveData<Event<Boolean>>()
    val notifyDeletion: LiveData<Event<Boolean>>
        get() = _notifyDeletion

    private val _updatedCartItem = MutableLiveData<CartItem2>()
    val updatedCartItem: LiveData<CartItem2>
        get() = _updatedCartItem

    private val _selectChangeId = MutableLiveData<Int>()
    val selectChangeId: LiveData<Int>
        get() = _selectChangeId

    val totalPrice: LiveData<Int>
        get() = selectedItems.map {
            it.sumOf { item -> item.totalPrice() }
        }

    private val cartItems: MutableList<CartItem2> = mutableListOf()

    private val _isBackButtonClicked = MutableLiveData<Event<Boolean>>()
    val isBackButtonClicked: LiveData<Event<Boolean>>
        get() = _isBackButtonClicked

    private val _isEntireCheckboxSelected = MutableLiveData(false)
    val isEntireCheckboxSelected: LiveData<Boolean>
        get() = _isEntireCheckboxSelected

    private val _selectedItems = MutableLiveData<List<CartItem2>>(emptyList())
    val selectedItems: LiveData<List<CartItem2>>
        get() = _selectedItems

    init {
        loadPage()
    }

    fun loadPage() {
        runCatching {
            val totalQuantity = cartRepository.getCartTotalQuantity().getOrNull()?.quantity ?: 0
            val cartItemResponse = cartRepository.getCartItems(0, totalQuantity, "asc")
                .getOrNull()
            cartItems.addAll(cartItemResponse?.cartItems ?: emptyList())
            _cartUiState.value = UIState.Success(cartItems)
            println("cartUiState : ${cartUiState.value}")
        }.onFailure {
            _cartUiState.value = UIState.Error(it)
        }
    }

    fun deleteItem(itemId: Int) {
        cartRepository.deleteCartItem(itemId)
        println("cartItems1111 : $cartItems")
        println("itemId : $itemId")
        val position = cartItems.indexOfFirst { it.cartItemId == itemId }
        cartItems.removeAt(position)
        _cartUiState.value = UIState.Success(cartItems)
    }

    fun selectAll(isChecked: Boolean) {
        if (isChecked) {
            _selectedItems.value = cartItems
        } else {
            _selectedItems.value = mutableListOf()
        }
    }

    override fun onCartItemClick(productId: Int) {
        _navigateToDetail.value = Event(productId)
    }

    override fun onDeleteButtonClick(itemId: Int) {
        deleteItem(itemId)
        val cartItem = cartItems.first { it.cartItemId == itemId }
        if (selectedItems.value?.contains(cartItem) == true) {
            _selectedItems.value = selectedItems.value?.minus(cartItem)
        }
        _notifyDeletion.value = Event(true)
    }

    override fun onSelectChanged(itemId: Int, isSelected: Boolean) {
        val cartItem = cartItems.first { it.cartItemId == itemId }
        if (isSelected) {
            _selectedItems.value = selectedItems.value?.minus(cartItem)
        } else {
            _selectedItems.value = selectedItems.value?.plus(cartItem)
        }
        _selectChangeId.value = itemId
    }

    override fun onQuantityPlusButtonClick(productId: Int) {
        var cartItem = cartItems.first { it.product.id == productId }
        cartItem = cartItem.plusQuantity()
        val position = cartItems.indexOfFirst { it.cartItemId == cartItem.cartItemId }
        cartItems[position] = cartItem
        if(selectedItems.value?.contains(cartItem) == true) {
            val selectedPosition = selectedItems.value?.indexOfFirst { it.product.id == productId } ?: return
            (_selectedItems.value as MutableList)[selectedPosition] = cartItem
        }
        println("${cartItem.cartItemId} ${cartItem.quantity}")
        cartRepository.updateCartItem(cartItem.cartItemId, cartItem.quantity)
        _updatedCartItem.value = cartItem
    }

    override fun onQuantityMinusButtonClick(productId: Int) {
        var cartItem = cartItems.first { it.product.id == productId }
        cartItem = cartItem.minusQuantity()
        val position = cartItems.indexOfFirst { it.cartItemId == cartItem.cartItemId }
        cartItems[position] = cartItem
        if(selectedItems.value?.contains(cartItem) == true) {
            val selectedPosition = selectedItems.value?.indexOfFirst { it.product.id == productId } ?: return
            (_selectedItems.value as MutableList)[selectedPosition] = cartItem
        }
        println("${cartItem.cartItemId} ${cartItem.quantity}")
        cartRepository.updateCartItem(cartItem.cartItemId, cartItem.quantity)
        _updatedCartItem.value = cartItem
    }

    fun onBackButtonClick() {
        _isBackButtonClicked.value = Event(true)
    }
}
