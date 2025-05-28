package woowacourse.shopping.view.shoppingCart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.shoppingCart.repository.CartRepository
import woowacourse.shopping.data.shoppingCart.repository.DefaultCartRepository
import woowacourse.shopping.domain.cart.PageableCartItems
import woowacourse.shopping.domain.product.CartItem
import woowacourse.shopping.view.MutableSingleLiveData
import woowacourse.shopping.view.SingleLiveData

class ShoppingCartViewModel(
    private val cartRepository: CartRepository = DefaultCartRepository(),
) : ViewModel() {
    private var page: Int = FIRST_PAGE

    private val _shoppingCartItems: MutableLiveData<List<ShoppingCartItem>> = MutableLiveData()
    val shoppingCartItems: LiveData<List<ShoppingCartItem>> get() = _shoppingCartItems

    private val _event: MutableSingleLiveData<ShoppingCartEvent> = MutableSingleLiveData()
    val event: SingleLiveData<ShoppingCartEvent> get() = _event

    init {
        loadShoppingCart()
    }

    private fun loadShoppingCart() {
        cartRepository.loadPageableCartItems(
            page - 1,
            COUNT_PER_PAGE,
        ) { result: Result<PageableCartItems> ->
            result
                .onSuccess { pageableCartItems: PageableCartItems ->
                    _shoppingCartItems.postValue(pageableCartItems.toShoppingCartItems())
                }.onFailure {
                    _event.postValue(ShoppingCartEvent.LOAD_SHOPPING_CART_FAILURE)
                }
        }
    }

    private fun PageableCartItems.toShoppingCartItems(): List<ShoppingCartItem> {
        val paginationItem =
            ShoppingCartItem.PaginationItem(
                page = page,
                previousEnabled = hasPrevious,
                nextEnabled = hasNext,
            )

        return this.cartItems.map(ShoppingCartItem::ProductItem) + paginationItem
    }

    fun removeShoppingCartProduct(cartItem: CartItem) {
        cartRepository.remove(cartItem.id) { result: Result<Unit> ->
            result
                .onSuccess {
                    loadShoppingCart()
                }.onFailure {
                    _event.postValue(ShoppingCartEvent.REMOVE_SHOPPING_CART_PRODUCT_FAILURE)
                }
        }
    }

    fun plusPage() {
        page++
        loadShoppingCart()
    }

    fun minusPage() {
        if (page != FIRST_PAGE) {
            page--
            loadShoppingCart()
        }
    }

    fun updateShoppingCart(): Any? = TODO()

    companion object {
        private const val FIRST_PAGE = 1
        private const val COUNT_PER_PAGE = 5
    }
}
