package woowacourse.shopping.view.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import woowacourse.shopping.data.cart.repository.CartRepository
import woowacourse.shopping.data.cart.repository.DefaultCartRepository
import woowacourse.shopping.domain.cart.PageableCartItems
import woowacourse.shopping.domain.product.CartItem
import woowacourse.shopping.view.MutableSingleLiveData
import woowacourse.shopping.view.SingleLiveData

class CartViewModel(
    private val cartRepository: CartRepository = DefaultCartRepository(),
) : ViewModel() {
    private val _cartItems: MutableLiveData<List<CartItemType>> = MutableLiveData()
    val cartItems: LiveData<List<CartItemType>> get() = _cartItems

    private val _event: MutableSingleLiveData<CartEvent> = MutableSingleLiveData()
    val event: SingleLiveData<CartEvent> get() = _event

    private var page: Int = MIN_PAGE

    val loading: MutableLiveData<Boolean> = MutableLiveData(true)

    private val _selectedCartItems: MutableLiveData<Set<CartItemType.ProductItem>> =
        MutableLiveData(emptySet())
    val selectedCartItems: LiveData<Set<CartItemType.ProductItem>>
        get() = _selectedCartItems

    private val _totalPrice: MutableLiveData<Int> = MutableLiveData(0)
    val totalPrice: LiveData<Int> =
        selectedCartItems.map { selectedCartItems ->
            selectedCartItems.sumOf { productItem: CartItemType.ProductItem ->
                productItem.price
            }
        }

    init {
        loadCartItems()
    }

    fun select(cartItem: CartItemType.ProductItem) {
        _selectedCartItems.value = selectedCartItems.value?.minus(cartItem) ?: emptySet()
        _selectedCartItems.value = selectedCartItems.value?.plus(cartItem) ?: setOf(cartItem)
        _totalPrice.value = selectedCartItems.value?.sumOf { it.price }
    }

    fun unselect(cartItem: CartItemType.ProductItem) {
        _selectedCartItems.value = selectedCartItems.value?.minus(cartItem) ?: emptySet()
    }

    fun plusCartItemQuantity(cartItem: CartItemType.ProductItem) {
        cartRepository.updateCartItemQuantity(
            cartItem.cartItem.id,
            cartItem.quantity + 1,
        ) { result ->
            result
                .onSuccess {
                    loadCartItems()
                }.onFailure {
                    _event.postValue(CartEvent.PLUS_CART_ITEM_QUANTITY_FAILURE)
                }
        }
    }

    fun minusCartItemQuantity(cartItem: CartItemType.ProductItem) {
        val cartItemId = cartItem.cartItem.id
        if (cartItem.quantity == 1) {
            cartRepository.remove(cartItemId) { result ->
                result
                    .onSuccess {
                        loadCartItems()
                        val oldSelectedCartItems: Set<CartItemType.ProductItem> =
                            selectedCartItems.value ?: emptySet()
                        val newSelectedCartItems: Set<CartItemType.ProductItem> =
                            (oldSelectedCartItems - cartItem)
                        _selectedCartItems.postValue(newSelectedCartItems)
                    }.onFailure {
                        _event.postValue(CartEvent.MINUS_CART_ITEM_QUANTITY_FAILURE)
                    }
            }
        } else {
            cartRepository.updateCartItemQuantity(cartItemId, cartItem.quantity - 1) { result ->
                result
                    .onSuccess {
                        loadCartItems()
                    }.onFailure {
                        _event.postValue(CartEvent.MINUS_CART_ITEM_QUANTITY_FAILURE)
                    }
            }
        }
    }

    private fun loadCartItems() {
        cartRepository.loadPageableCartItems(page - 1, COUNT_PER_PAGE) { result ->
            result
                .onSuccess { pageableCartItems: PageableCartItems ->
                    val cartItems: List<CartItemType.ProductItem> =
                        pageableCartItems.cartItems.map { newCartItem: CartItem ->
                            val checked =
                                selectedCartItems.value?.any { selectedProductItem: CartItemType.ProductItem ->
                                    newCartItem == selectedProductItem.cartItem
                                } ?: false

                            val newProductItem: CartItemType.ProductItem =
                                CartItemType.ProductItem(newCartItem, checked)

                            if (checked) {
                                val oldSelectedCartItems: Set<CartItemType.ProductItem> =
                                    selectedCartItems.value ?: emptySet()
                                val newSelectedCartItems: Set<CartItemType.ProductItem> =
                                    oldSelectedCartItems.minus(newProductItem).plus(newProductItem)
                                _selectedCartItems.postValue(newSelectedCartItems)
                            }

                            newProductItem
                        }
                    val paginationItem: CartItemType.PaginationItem =
                        CartItemType.PaginationItem(
                            page = page,
                            previousEnabled = pageableCartItems.hasPrevious,
                            nextEnabled = pageableCartItems.hasNext,
                        )

                    _cartItems.postValue(cartItems + paginationItem)
                }.onFailure {
                    _event.postValue(CartEvent.LOAD_SHOPPING_CART_FAILURE)
                }
            Thread.sleep(1000)
            loading.postValue(false)
        }
    }

    fun removeCartItem(cartItem: CartItemType.ProductItem) {
        cartRepository.remove(cartItem.cartItemId) { result ->
            result
                .onSuccess {
                    if (_cartItems.value?.size == 1) {
                        minusPage()
                    } else {
                        loadCartItems()
                    }
                    val oldSelectedCartItems: Set<CartItemType.ProductItem> =
                        selectedCartItems.value ?: emptySet()
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

    companion object {
        private const val MIN_PAGE = 1
        private const val COUNT_PER_PAGE = 5
    }
}
