package woowacourse.shopping.view.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import woowacourse.shopping.data.cart.repository.CartRepository
import woowacourse.shopping.data.cart.repository.DefaultCartRepository
import woowacourse.shopping.domain.Pageable
import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.view.MutableSingleLiveData
import woowacourse.shopping.view.SingleLiveData

class CartViewModel(
    private val cartRepository: CartRepository = DefaultCartRepository(),
) : ViewModel() {
    private val _event: MutableSingleLiveData<CartEvent> = MutableSingleLiveData()
    val event: SingleLiveData<CartEvent> get() = _event

    private val _loading: MutableLiveData<Boolean> = MutableLiveData(true)
    val loading: LiveData<Boolean> get() = _loading

    private var page: Int = MIN_PAGE

    private val _cartItems: MutableLiveData<List<CartItemType>> = MutableLiveData()
    val cartItems: LiveData<List<CartItemType>> get() = _cartItems

    val selectedCartItems: MutableLiveData<Set<CartItemType.ProductItem>> =
        MutableLiveData(emptySet())
    val totalPrice: LiveData<Int> = selectedCartItems.map { it.sumOf { it.price } }
    val selectedCartItemsCount = selectedCartItems.map { it.sumOf { it.quantity } }
    val allSelected: LiveData<Boolean> =
        cartItems.map {
            it
                .filterIsInstance<CartItemType.ProductItem>()
                .all { it.selected }
        }
    val orderable = selectedCartItems.map { it.isNotEmpty() }

    init {
        loadCartItems()
    }

    fun loadCartItems() {
        viewModelScope.launch {
            cartRepository
                .loadPageableCartItems(page - 1, PAGE_SIZE)
                .onSuccess { pageableCartItems: Pageable<CartItem> ->
                    _cartItems.value =
                        pageableCartItems.productItems() + pageableCartItems.paginationItem()
                }.onFailure {
                    _event.value = CartEvent.LOAD_SHOPPING_CART_FAILURE
                }
            delay(LOADING_TIME)
            _loading.value = false
        }
    }

    fun minusPage() {
        page = (page - 1).coerceAtLeast(MIN_PAGE)
        loadCartItems()
    }

    fun plusPage() {
        page++
        loadCartItems()
    }

    fun removeCartItem(cartItem: CartItemType.ProductItem) {
        viewModelScope.launch {
            cartRepository
                .remove(cartItem.cartItemId)
                .onSuccess {
                    selectedCartItems.value = selectedCartItems.value?.minus(cartItem) ?: emptySet()

                    if (_cartItems.value?.size == 1) {
                        minusPage()
                    } else {
                        loadCartItems()
                    }
                }.onFailure {
                    _event.value = CartEvent.REMOVE_SHOPPING_CART_PRODUCT_FAILURE
                }
        }
    }

    fun plusCartItemQuantity(cartItem: CartItemType.ProductItem) {
        viewModelScope.launch {
            cartRepository
                .updateCartItemQuantity(
                    cartItem.cartItemId,
                    cartItem.quantity + 1,
                ).onSuccess {
                    loadCartItems()
                }.onFailure {
                    _event.value = CartEvent.PLUS_CART_ITEM_QUANTITY_FAILURE
                }
        }
    }

    fun minusCartItemQuantity(cartItem: CartItemType.ProductItem) {
        viewModelScope.launch {
            val newQuantity = cartItem.quantity - 1
            if (newQuantity == 0) {
                removeCartItem(cartItem)
            } else {
                cartRepository
                    .updateCartItemQuantity(
                        cartItem.cartItemId,
                        newQuantity,
                    ).onSuccess {
                        loadCartItems()
                    }.onFailure {
                        _event.value = CartEvent.PLUS_CART_ITEM_QUANTITY_FAILURE
                    }
            }
        }
    }

    fun select(cartItem: CartItemType.ProductItem) {
        val newCartItem = cartItem.copy(selected = true)
        val index: Int = cartItems.value?.indexOf(cartItem) ?: 0
        val before: List<CartItemType> = cartItems.value?.slice(0 until index) ?: emptyList()
        val after: List<CartItemType> =
            cartItems.value?.slice(index + 1 until (cartItems.value?.size ?: 0)) ?: emptyList()

        _cartItems.value = before + newCartItem + after

        selectedCartItems.value =
            selectedCartItems.value?.minus(cartItem)?.plus(newCartItem) ?: setOf(newCartItem)
    }

    fun unselect(cartItem: CartItemType.ProductItem) {
        val newCartItem = cartItem.copy(selected = false)
        val index: Int = cartItems.value?.indexOf(cartItem) ?: 0
        val before: List<CartItemType> = cartItems.value?.slice(0 until index) ?: emptyList()
        val after: List<CartItemType> =
            cartItems.value?.slice(index + 1 until (cartItems.value?.size ?: 0)) ?: emptyList()

        _cartItems.value = before + newCartItem + after

        selectedCartItems.value = selectedCartItems.value?.minus(cartItem) ?: emptySet()
    }

    fun selectAll() {
        if (cartItems.value
                ?.filterIsInstance<CartItemType.ProductItem>()
                ?.all { it.selected == true } == true
        ) {
            return
        }
        _cartItems.value =
            cartItems.value?.map { if (it is CartItemType.ProductItem) it.copy(selected = true) else it }

        removeUnselectedItems()
    }

    fun unselectAll() {
        if (cartItems.value
                ?.filterIsInstance<CartItemType.ProductItem>()
                ?.all { it.selected == false } == true
        ) {
            return
        }
        _cartItems.value =
            cartItems.value?.map { if (it is CartItemType.ProductItem) it.copy(selected = false) else it }

        removeUnselectedItems()
    }

    private fun Pageable<CartItem>.productItems(): List<CartItemType.ProductItem> =
        items.map { cartItem: CartItem ->
            val checked =
                selectedCartItems.value?.any { productItem -> productItem.cartItemId == cartItem.id } == true
            CartItemType.ProductItem(cartItem, checked)
        }

    private fun Pageable<CartItem>.paginationItem(): CartItemType.PaginationItem =
        CartItemType.PaginationItem(
            page = page,
            previousEnabled = hasPrevious,
            nextEnabled = hasNext,
        )

    private fun removeUnselectedItems() {
        selectedCartItems.value =
            cartItems.value
                ?.filterIsInstance<CartItemType.ProductItem>()
                ?.filter { it.selected }
                ?.toSet() ?: emptySet()
    }

    private companion object {
        private const val MIN_PAGE = 1
        private const val PAGE_SIZE = 5
        private const val LOADING_TIME = 500L
    }
}
