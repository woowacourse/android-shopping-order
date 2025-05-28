package woowacourse.shopping.view.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.cart.repository.CartRepository
import woowacourse.shopping.data.cart.repository.DefaultCartRepository
import woowacourse.shopping.domain.cart.PageableCartItems
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

    init {
        loadCartItems()
    }

    private fun loadCartItems() {
        cartRepository.loadPageableCartItems(page - 1, COUNT_PER_PAGE) { result ->
            result
                .onSuccess { pageableCartItems: PageableCartItems ->
                    val cartItems: List<CartItemType> =
                        pageableCartItems.cartItems.map {
                            CartItemType.ProductItem(it)
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

    fun removeCartItem(cartItemId: Long) {
        cartRepository.remove(cartItemId) { result ->
            result
                .onSuccess {
                    if (_cartItems.value?.size == 1) {
                        minusPage()
                    } else {
                        loadCartItems()
                    }
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
