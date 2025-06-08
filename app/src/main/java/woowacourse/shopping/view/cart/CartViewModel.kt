package woowacourse.shopping.view.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.cart.repository.CartRepository
import woowacourse.shopping.data.cart.repository.DefaultCartRepository
import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.domain.cart.PagedCartItems
import woowacourse.shopping.view.MutableSingleLiveData
import woowacourse.shopping.view.SingleLiveData

class CartViewModel(
    private val cartRepository: CartRepository = DefaultCartRepository(),
) : ViewModel() {
    private val _cartItems: MutableLiveData<List<CartItemType>> = MutableLiveData()
    val cartItems: LiveData<List<CartItemType>> get() = _cartItems

    private var cartProducts: List<CartItem> = emptyList()

    private val _isSelectAll: MutableLiveData<Boolean> = MutableLiveData()
    val isSelectAll: LiveData<Boolean> = _isSelectAll

    private val _event: MutableSingleLiveData<CartEvent> = MutableSingleLiveData()
    val event: SingleLiveData<CartEvent> get() = _event

    private var page: Int = MIN_PAGE

    val loading: MutableLiveData<Boolean> = MutableLiveData(true)

    private val _selectedCartItems: MutableLiveData<Set<CartItemType.ProductItem>> =
        MutableLiveData(emptySet())
    val selectedCartItems: LiveData<Set<CartItemType.ProductItem>>
        get() = _selectedCartItems

    val totalPrice: LiveData<Int> =
        selectedCartItems.map { selectedCartItems ->
            selectedCartItems.sumOf { productItem: CartItemType.ProductItem ->
                productItem.price
            }
        }

    val totalQuantity: LiveData<Int> =
        selectedCartItems.map { selectedCartItems ->
            selectedCartItems.sumOf { productItem: CartItemType.ProductItem ->
                productItem.quantity
            }
        }

    init {
        loadCartItems()
        loadAllProducts()
    }

    private fun loadAllProducts() {
        viewModelScope.launch {
            runCatching {
                cartRepository.loadCart()
            }.onSuccess { cartItems: List<CartItem> ->
                cartProducts = cartItems
            }.onFailure {
                _event.postValue(CartEvent.LOAD_SHOPPING_CART_FAILURE)
            }
        }
    }

    fun selectAll() {
        _selectedCartItems.value = emptySet()
        _selectedCartItems.value =
            cartProducts
                .map { cartItem ->
                    CartItemType.ProductItem(
                        cartItem = cartItem,
                        checked = true,
                    )
                }.toSet()
        _isSelectAll.postValue(true)
    }

    fun unselectAll() {
        _selectedCartItems.value = emptySet()
        _isSelectAll.postValue(false)
    }

    fun select(cartItem: CartItemType.ProductItem) {
        val oldSet: Set<CartItemType.ProductItem> = selectedCartItems.value.orEmpty()
        val filtered: Set<CartItemType.ProductItem> = oldSet.filterOutById(cartItem.cartItemId)
        _selectedCartItems.value = filtered + cartItem
    }

    fun unselect(cartItem: CartItemType.ProductItem) {
        val updated =
            selectedCartItems.value
                .orEmpty()
                .filterOutById(cartItem.cartItemId)
                .toSet()
        _selectedCartItems.value = updated
    }

    fun plusCartItemQuantity(cartItem: CartItemType.ProductItem) {
        viewModelScope.launch {
            runCatching {
                cartRepository.updateCartItemQuantity(
                    cartItem.cartItem.id,
                    cartItem.quantity + 1,
                )
            }.onSuccess {
                loadCartItems()
            }.onFailure {
                _event.postValue(CartEvent.PLUS_CART_ITEM_QUANTITY_FAILURE)
            }
        }
    }

    fun minusCartItemQuantity(cartItem: CartItemType.ProductItem) {
        val cartItemId = cartItem.cartItem.id

        viewModelScope.launch {
            if (cartItem.quantity == 1) {
                runCatching {
                    cartRepository.remove(cartItemId)
                }.onSuccess {
                    _selectedCartItems.postValue(
                        run {
                            selectedCartItems.value
                                ?.filterOutById(cartItem.cartItemId)
                                ?.toSet()
                        },
                    )
                    loadCartItems()
                }.onFailure {
                    _event.postValue(CartEvent.MINUS_CART_ITEM_QUANTITY_FAILURE)
                }
            } else {
                runCatching {
                    cartRepository.updateCartItemQuantity(cartItemId, cartItem.quantity - 1)
                }.onSuccess {
                    loadCartItems()
                }.onFailure {
                    _event.postValue(CartEvent.MINUS_CART_ITEM_QUANTITY_FAILURE)
                }
            }
        }
    }

    fun loadCartItems() {
        viewModelScope.launch {
            runCatching {
                cartRepository.loadPagedCartItems(page - 1, COUNT_PER_PAGE)
            }.onSuccess { pagedCartItems: PagedCartItems ->
                val cartItems: List<CartItemType.ProductItem> =
                    pagedCartItems.cartItems.map { newCartItem: CartItem ->
                        val selectedItem =
                            selectedCartItems.value
                                .orEmpty()
                                .find { it.cartItem.id == newCartItem.id }

                        val checked = selectedItem != null

                        val newProductItem = CartItemType.ProductItem(newCartItem, checked)

                        if (checked) {
                            val updated =
                                selectedCartItems.value
                                    .orEmpty()
                                    .filterOutById(newCartItem.id)
                                    .toSet() + newProductItem
                            _selectedCartItems.postValue(updated)
                        }

                        newProductItem
                    }
                val paginationItem: CartItemType.PaginationItem =
                    CartItemType.PaginationItem(
                        page = page,
                        previousEnabled = pagedCartItems.hasPrevious,
                        nextEnabled = pagedCartItems.hasNext,
                    )

                _cartItems.postValue(cartItems + paginationItem)
            }.onFailure {
                _event.postValue(CartEvent.LOAD_SHOPPING_CART_FAILURE)
            }
            loading.postValue(false)
        }
    }

    fun removeCartItem(cartItem: CartItemType.ProductItem) {
        viewModelScope.launch {
            runCatching {
                cartRepository.remove(cartItem.cartItemId)
            }.onSuccess {
                if (_cartItems.value?.size == 1) {
                    minusPage()
                } else {
                    loadCartItems()
                }
                val oldSelectedCartItems: Set<CartItemType.ProductItem> =
                    selectedCartItems.value.orEmpty()
                val newSelectedCartItems: Set<CartItemType.ProductItem> =
                    (oldSelectedCartItems - cartItem)
                _selectedCartItems.postValue(newSelectedCartItems)
            }.onFailure {
                _event.postValue(CartEvent.REMOVE_SHOPPING_CART_PRODUCT_FAILURE)
            }
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

    private fun Set<CartItemType.ProductItem>.filterOutById(id: Long): Set<CartItemType.ProductItem> =
        this
            .filterNot {
                it.cartItemId == id
            }.toSet()

    companion object {
        private const val MIN_PAGE = 1
        private const val COUNT_PER_PAGE = 5
    }
}
