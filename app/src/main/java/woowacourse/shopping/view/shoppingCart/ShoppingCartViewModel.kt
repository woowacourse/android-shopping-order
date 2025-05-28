package woowacourse.shopping.view.shoppingCart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.shoppingCart.repository.DefaultShoppingCartRepository
import woowacourse.shopping.data.shoppingCart.repository.ShoppingCartRepository
import woowacourse.shopping.domain.cart.PageableCartItems
import woowacourse.shopping.domain.product.CartItem
import woowacourse.shopping.view.MutableSingleLiveData
import woowacourse.shopping.view.SingleLiveData

class ShoppingCartViewModel(
    private val shoppingCartRepository: ShoppingCartRepository = DefaultShoppingCartRepository(),
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
        shoppingCartRepository.loadPageableCartItems(
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

    fun updateShoppingCart() {
        val productItems: List<ShoppingCartItem.ProductItem> =
            shoppingCartItems.value?.filterIsInstance<ShoppingCartItem.ProductItem>() ?: return

        val cartItemsToUpdate: List<CartItem> =
            productItems
                .map { productItem: ShoppingCartItem.ProductItem ->
                    CartItem(
                        productItem.cartItem.productId,
                        productItem.cartItem.name,
                        productItem.cartItem.price,
                        productItem.quantity,
                    )
                }.filter { cartItem -> cartItem.quantity != 0 }

        shoppingCartRepository.update(cartItemsToUpdate) { result ->
            result
                .onSuccess {
                    _event.postValue(ShoppingCartEvent.UPDATE_SHOPPING_CART_PRODUCT_SUCCESS)
                }.onFailure {
                    _event.postValue(ShoppingCartEvent.UPDATE_SHOPPING_CART_PRODUCT_FAILURE)
                }
        }
    }

    fun removeShoppingCartProduct(cartItem: CartItem) {
        shoppingCartRepository.remove(cartItem) { result: Result<Unit> ->
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

    companion object {
        private const val FIRST_PAGE = 1
        private const val COUNT_PER_PAGE = 5
    }
}
