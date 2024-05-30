package woowacourse.shopping.view.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import woowacourse.shopping.data.model.CartItem2
import woowacourse.shopping.domain.repository.CartRepository2
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.ProductRepository2
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.util.Event
import woowacourse.shopping.view.cart.adapter.ShoppingCartViewItem
import woowacourse.shopping.view.cart.adapter.ShoppingCartViewItem.CartViewItem
import woowacourse.shopping.view.home.adapter.product.HomeViewItem
import woowacourse.shopping.view.state.UIState

class CartViewModel(
    private val cartRepository: CartRepository2,
    private val orderRepository: OrderRepository,
    private val recentProductRepository: RecentProductRepository,
    private val productRepository: ProductRepository2,
) :
    ViewModel(),
    CartItemClickListener,
    QuantityClickListener {

    private val _isCartScreen = MutableLiveData(true)
    val isCartScreen: LiveData<Boolean>
        get() = _isCartScreen

    private val _cartUiState =
        MutableLiveData<UIState<List<ShoppingCartViewItem.CartViewItem>>>(UIState.Loading)
    val cartUiState: LiveData<UIState<List<ShoppingCartViewItem.CartViewItem>>>
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

    private val _navigateToRecommend = MutableLiveData<Event<Unit>>()
    val navigateToRecommend: LiveData<Event<Unit>>
        get() = _navigateToRecommend

    private val _navigateBack = MutableLiveData<Event<Unit>>()
    val navigateBack: LiveData<Event<Unit>>
        get() = _navigateBack

    private val _notifyDeletion = MutableLiveData<Event<Boolean>>()
    val notifyDeletion: LiveData<Event<Boolean>>
        get() = _notifyDeletion

    private val _updatedCartItem = MutableLiveData<CartItem2>()
    val updatedCartItem: LiveData<CartItem2>
        get() = _updatedCartItem

    private val _selectChangeId = MutableLiveData<Int>()
    val selectChangeId: LiveData<Int>
        get() = _selectChangeId

    private val _totalPrice = MutableLiveData(0)
    val totalPrice: LiveData<Int>
        get() = _totalPrice

    private val cartItems: MutableList<ShoppingCartViewItem.CartViewItem> = mutableListOf()

    private val _isBackButtonClicked = MutableLiveData<Event<Boolean>>()
    val isBackButtonClicked: LiveData<Event<Boolean>>
        get() = _isBackButtonClicked

    private val _isEntireCheckboxSelected = MutableLiveData(false)
    val isEntireCheckboxSelected: LiveData<Boolean>
        get() = _isEntireCheckboxSelected

    private val _selectedItems =
        MutableLiveData<List<ShoppingCartViewItem.CartViewItem>>(emptyList())
    val selectedItems: LiveData<List<ShoppingCartViewItem.CartViewItem>>
        get() = _selectedItems

    val recentProduct = recentProductRepository.findMostRecentProduct()

    private val _recommendedProducts =
        MutableLiveData<List<HomeViewItem.ProductViewItem>>(emptyList())
    val recommendedProducts: LiveData<List<HomeViewItem.ProductViewItem>>
        get() = _recommendedProducts

    init {
        loadPage()
    }

    fun loadPage() {
        runCatching {
            val totalQuantity = cartRepository.getCartTotalQuantity().getOrNull()?.quantity ?: 0
            val cartItemResponse = cartRepository.getCartItems(0, totalQuantity, "asc")
                .getOrNull()
            val cartViewItems = (cartItemResponse?.cartItems?.map(::CartViewItem) ?: emptyList())
            cartItems.addAll(cartViewItems)
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
        val position = cartItems.indexOfFirst { it.cartItem.cartItemId == itemId }
        cartItems.removeAt(position)
        _cartUiState.value = UIState.Success(cartItems)
        _totalPrice.value = selectedItems.value?.sumOf { item ->
            item.cartItem.totalPrice()
        }
    }

    fun selectAll(isChecked: Boolean) {
        if (isChecked) {
            _selectedItems.value = cartItems
            _totalPrice.value = selectedItems.value?.sumOf { item ->
                item.cartItem.totalPrice()
            }
        } else {
            _selectedItems.value = mutableListOf()
            _totalPrice.value = selectedItems.value?.sumOf { item ->
                item.cartItem.totalPrice()
            }
        }
    }

    override fun onCartItemClick(productId: Int) {
        _navigateToDetail.value = Event(productId)
    }

    override fun onDeleteButtonClick(itemId: Int) {
        deleteItem(itemId)
        val cartItem = cartItems.firstOrNull { it.cartItem.cartItemId == itemId } ?: return
        if (selectedItems.value?.contains(cartItem) == true) {
            _selectedItems.value = selectedItems.value?.minus(cartItem)
        }
        _notifyDeletion.value = Event(true)
    }

    override fun onSelectChanged(itemId: Int, isSelected: Boolean) {
        val cartItem = cartItems.firstOrNull { it.cartItem.cartItemId == itemId } ?: return
        if (selectedItems.value?.contains(cartItem) == false && isSelected) {
            _selectedItems.value = selectedItems.value?.plus(cartItem)
        } else {
            _selectedItems.value = selectedItems.value?.minus(cartItem)
        }
        _selectChangeId.value = itemId
        _totalPrice.value = selectedItems.value?.sumOf { item ->
            item.cartItem.totalPrice()
        }
    }

    override fun onQuantityPlusButtonClick(productId: Int) {
        var targetCartItem = cartItems.firstOrNull { it.cartItem.product.id == productId } ?: return
        // TODO
        targetCartItem = targetCartItem.copy(
            cartItem = targetCartItem.cartItem.plusQuantity()
        )
        val position =
            cartItems.indexOfFirst { it.cartItem.cartItemId == targetCartItem.cartItem.cartItemId }
        cartItems[position] = targetCartItem
        if (selectedItems.value?.contains(targetCartItem) == true) {
            val selectedPosition =
                selectedItems.value?.indexOfFirst { it.cartItem.product.id == productId } ?: return
            (_selectedItems.value as MutableList)[selectedPosition] = targetCartItem
        }
        println("${targetCartItem.cartItem.cartItemId} ${targetCartItem.cartItem.quantity}")
        cartRepository.updateCartItem(
            targetCartItem.cartItem.cartItemId,
            targetCartItem.cartItem.quantity
        )
        _updatedCartItem.value = targetCartItem.cartItem
        _totalPrice.value = selectedItems.value?.sumOf { item ->
            item.cartItem.totalPrice()
        }
    }

    override fun onQuantityMinusButtonClick(productId: Int) {
        var targetCartItem = cartItems.firstOrNull { it.cartItem.product.id == productId } ?: return
        targetCartItem = targetCartItem.copy(
            cartItem = targetCartItem.cartItem.minusQuantity()
        )
//        cartItem = cartItem.cartItem.minusQuantity()
        val position =
            cartItems.indexOfFirst { it.cartItem.cartItemId == targetCartItem.cartItem.cartItemId }
        cartItems[position] = targetCartItem
        if (selectedItems.value?.contains(targetCartItem) == true) {
            val selectedPosition =
                selectedItems.value?.indexOfFirst { it.cartItem.product.id == productId } ?: return
            (_selectedItems.value as MutableList)[selectedPosition] = targetCartItem
        }
//        println("${cartItem.cartItemId} ${cartItem.quantity}")
        cartRepository.updateCartItem(
            targetCartItem.cartItem.cartItemId,
            targetCartItem.cartItem.quantity
        )
        _updatedCartItem.value = targetCartItem.cartItem
        _totalPrice.value = selectedItems.value?.sumOf { item ->
            item.cartItem.totalPrice()
        }
    }

    fun onNextButtonClick() {
        if (isCartScreen.value == true) {
            _navigateToRecommend.value = Event(Unit)
            _isCartScreen.value = false
        } else {
            val result = orderRepository.postOrder(selectedItems.value?.map {
                it.cartItem.cartItemId
            } ?: emptyList()).getOrNull()
            if (result != null) _navigateBack.value = Event(Unit)
        }
    }

    fun getRecommendedItems() {
        val response =
            productRepository.getProducts(recentProduct?.category, 0, 10, "asc").getOrNull()
    }

    fun onBackButtonClick() {
        _isBackButtonClicked.value = Event(true)
    }
}
