package woowacourse.shopping.view.shoppingCart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.shoppingCart.repository.DefaultShoppingCartRepository
import woowacourse.shopping.data.shoppingCart.repository.ShoppingCartRepository
import woowacourse.shopping.domain.shoppingCart.ShoppingCartProduct
import woowacourse.shopping.view.common.MutableSingleLiveData
import woowacourse.shopping.view.common.SingleLiveData
import woowacourse.shopping.view.shoppingCart.ShoppingCartItem.PaginationItem
import woowacourse.shopping.view.shoppingCart.ShoppingCartItem.ShoppingCartProductItem

class ShoppingCartViewModel(
    private val shoppingCartRepository: ShoppingCartRepository = DefaultShoppingCartRepository.get(),
) : ViewModel() {
    private val _shoppingCart: MutableLiveData<List<ShoppingCartItem>> = MutableLiveData()
    val shoppingCart: LiveData<List<ShoppingCartItem>> get() = _shoppingCart

    private val _hasUpdatedProducts: MutableLiveData<Boolean> =
        MutableLiveData(false)
    val hasUpdatedProducts: LiveData<Boolean> get() = _hasUpdatedProducts

    private val _event: MutableSingleLiveData<ShoppingCartEvent> = MutableSingleLiveData()
    val event: SingleLiveData<ShoppingCartEvent> get() = _event

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _totalPrice: MediatorLiveData<Int> = MediatorLiveData<Int>().apply { value = 0 }
    val totalPrice: LiveData<Int> get() = _totalPrice

    private val _totalQuantity: MediatorLiveData<Int> = MediatorLiveData<Int>().apply { value = 0 }
    val totalQuantity: LiveData<Int> get() = _totalQuantity

    private var page: Int = MINIMUM_PAGE
    private var hasPreviousPage: Boolean = false
    private var hasNextPage: Boolean = false

    init {
        _totalPrice.addSource(_shoppingCart) { it ->
            _totalPrice.value =
                it
                    .filterIsInstance<ShoppingCartProductItem>()
                    .filter { item -> item.isChecked }
                    .sumOf { item -> item.shoppingCartProduct.price }
        }

        _totalQuantity.addSource(_shoppingCart) {
            _totalQuantity.value =
                it
                    .filterIsInstance<ShoppingCartProductItem>()
                    .filter { item -> item.isChecked }
                    .sumOf { item -> item.shoppingCartProduct.quantity }
        }
    }

    fun updateShoppingCart() {
        val size = COUNT_PER_PAGE + 1
        val page = this.page - 1
        shoppingCartRepository.load(page, size) { result ->
            result
                .onSuccess { shoppingCartProducts: List<ShoppingCartProduct> ->
                    if (isEmptyPage(shoppingCartProducts)) return@load

                    updatePaginationState(shoppingCartProducts)

                    val items = createShoppingCartItems(shoppingCartProducts)
                    _shoppingCart.value = items
                    _isLoading.value = false
                }.onFailure {
                    _event.postValue(ShoppingCartEvent.UPDATE_SHOPPING_CART_FAILURE)
                    _isLoading.value = false
                }
        }
    }

    private fun isEmptyPage(products: List<ShoppingCartProduct>): Boolean =
        if (products.isEmpty() && page != MINIMUM_PAGE) {
            minusPage()
            true
        } else {
            false
        }

    private fun updatePaginationState(products: List<ShoppingCartProduct>) {
        hasNextPage = products.size > COUNT_PER_PAGE
        hasPreviousPage = page > MINIMUM_PAGE
    }

    private fun createShoppingCartItems(products: List<ShoppingCartProduct>): List<ShoppingCartItem> {
        val visibleProducts = products.take(COUNT_PER_PAGE)
        val paginationItem =
            PaginationItem(
                page = page,
                nextEnabled = hasNextPage,
                previousEnabled = hasPreviousPage,
            )

        return visibleProducts.map(::ShoppingCartProductItem) + paginationItem
    }

    fun removeShoppingCartProduct(shoppingCartProductItem: ShoppingCartProductItem) {
        shoppingCartRepository.remove(shoppingCartProductItem.shoppingCartProduct.id) { result ->
            result
                .onSuccess {
                    updateShoppingCart()
                    _hasUpdatedProducts.postValue(true)
                }.onFailure {
                    _event.postValue(ShoppingCartEvent.REMOVE_SHOPPING_CART_PRODUCT_FAILURE)
                }
        }
    }

    fun decreaseQuantity(shoppingCartProductItem: ShoppingCartProductItem) {
        shoppingCartRepository.decreaseQuantity(
            shoppingCartProductItem.shoppingCartProduct.id,
            shoppingCartProductItem.shoppingCartProduct.quantity - 1,
        ) { result ->
            result
                .onSuccess {
                    updateShoppingCart()
                    _hasUpdatedProducts.value = true
                }.onFailure {
                    _event.postValue(ShoppingCartEvent.DECREASE_SHOPPING_CART_PRODUCT_FAILURE)
                }
        }
    }

    fun increaseQuantity(shoppingCartProductItem: ShoppingCartProductItem) {
        shoppingCartRepository.increaseQuantity(
            shoppingCartProductItem.shoppingCartProduct.id,
            shoppingCartProductItem.shoppingCartProduct.quantity + 1,
        ) { result ->
            result
                .onSuccess {
                    updateShoppingCart()
                    _hasUpdatedProducts.value = true
                }.onFailure {
                    _event.postValue(ShoppingCartEvent.ADD_SHOPPING_CART_PRODUCT_FAILURE)
                }
        }
    }

    fun plusPage() {
        page++
        updateShoppingCart()
    }

    fun minusPage() {
        page = page.minus(1).coerceAtLeast(MINIMUM_PAGE)
        updateShoppingCart()
    }

    fun selectShoppingCartProduct(
        shoppingCartProductItem: ShoppingCartProductItem,
        selected: Boolean,
    ) {
        _shoppingCart.value =
            _shoppingCart.value?.filterIsInstance<ShoppingCartProductItem>()?.map { item ->
                if (item.shoppingCartProduct.id == shoppingCartProductItem.shoppingCartProduct.id) {
                    return@map shoppingCartProductItem.copy(
                        isChecked = selected,
                    )
                }
                return@map item
            }
    }

    companion object {
        private const val MINIMUM_PAGE = 1
        private const val COUNT_PER_PAGE: Int = 5
    }
}
