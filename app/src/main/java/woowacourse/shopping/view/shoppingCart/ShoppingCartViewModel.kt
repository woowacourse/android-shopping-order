package woowacourse.shopping.view.shoppingCart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import woowacourse.shopping.data.shoppingCart.repository.DefaultShoppingCartRepository
import woowacourse.shopping.data.shoppingCart.repository.ShoppingCartRepository
import woowacourse.shopping.domain.shoppingCart.ShoppingCartProduct
import woowacourse.shopping.view.common.MutableSingleLiveData
import woowacourse.shopping.view.common.SingleLiveData
import woowacourse.shopping.view.product.ProductsEvent

class ShoppingCartViewModel(
    private val shoppingCartRepository: ShoppingCartRepository = DefaultShoppingCartRepository.Companion.get(),
) : ViewModel() {
    private val _shoppingCart: MutableLiveData<List<ShoppingCartItem.ShoppingCartProductItem>> =
        MutableLiveData(emptyList())
    val shoppingCart: LiveData<List<ShoppingCartItem.ShoppingCartProductItem>> get() = _shoppingCart

    private val _hasUpdatedProducts: MutableLiveData<Boolean> =
        MutableLiveData(false)
    val hasUpdatedProducts: LiveData<Boolean> get() = _hasUpdatedProducts

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    val orderBarState: LiveData<ShoppingCartItem.OrderBarItem> =
        shoppingCart.map {
            ShoppingCartItem.OrderBarItem(
                totalPrice =
                    it
                        .filter { item -> item.isChecked }
                        .sumOf { item -> item.shoppingCartProduct.price },
                totalQuantity =
                    it
                        .filter { item -> item.isChecked }
                        .sumOf { item -> item.shoppingCartProduct.quantity },
                isAllSelected = it.all { item -> item.isChecked },
                shoppingCartProductsToOrder =
                    it.filter { item -> item.isChecked }
                        .map { item -> item.shoppingCartProduct },
                isOrderEnabled = it.any { item -> item.isChecked },
            )
        }

    private val _shoppingCartProductsToOrder: MutableLiveData<List<ShoppingCartProduct>> =
        MutableLiveData(emptyList())
    val shoppingCartProductsToOrder: LiveData<List<ShoppingCartProduct>> get() = _shoppingCartProductsToOrder

    private var page: Int = MINIMUM_PAGE

    private var loadable: Boolean = false

    private var isApiLoading: Boolean = false

    private val _event: MutableSingleLiveData<ProductsEvent> = MutableSingleLiveData()
    val event: SingleLiveData<ProductsEvent> get() = _event

    private val _orderEvent: MutableSingleLiveData<OrderEvent> = MutableSingleLiveData()
    val orderEvent: SingleLiveData<OrderEvent> get() = _orderEvent

    private val handler =
        CoroutineExceptionHandler { _, exception ->
            _event.postValue(ProductsEvent.UPDATE_PRODUCT_FAILURE)
            _isLoading.value = false
        }

    init {
        loadShoppingCart()
    }

    fun reload() {
        _shoppingCart.value = emptyList()
        loadShoppingCart()
    }

    private fun loadShoppingCart() {
        val page = this.page - 1
        val size = COUNT_PER_PAGE

        viewModelScope.launch(handler) {
            val shoppingCarts = shoppingCartRepository.load(page, size).getOrThrow()
            loadable = !shoppingCarts.last

            loadShoppingCartItems(
                shoppingCarts.shoppingCartItems,
            )
            _isLoading.value = false
        }
    }

    private fun loadShoppingCartItems(products: List<ShoppingCartProduct>) {
        _shoppingCart.value =
            _shoppingCart.value?.plus(
                buildList {
                    addAll(products.map { ShoppingCartItem.ShoppingCartProductItem(it) })
                },
            )
    }

    private suspend fun updateShoppingCartItems() {
        val shoppingCarts =
            shoppingCartRepository.load(0, COUNT_PER_PAGE * page).getOrThrow().shoppingCartItems
        _shoppingCart.value =
            _shoppingCart.value?.mapNotNull { item ->
                shoppingCarts.find { it.id == item.shoppingCartProduct.id }
                    ?.let { foundProduct ->
                        item.copy(
                            shoppingCartProduct = foundProduct,
                            isChecked = item.isChecked,
                        )
                    }
            }
    }

    fun removeShoppingCartProduct(shoppingCartProductItem: ShoppingCartItem.ShoppingCartProductItem) {
        if (isApiLoading) return
        isApiLoading = true
        viewModelScope.launch(handler) {
            shoppingCartRepository.remove(shoppingCartProductItem.shoppingCartProduct.id)
                .getOrThrow()
            updateShoppingCartItems()
            _hasUpdatedProducts.postValue(true)
            isApiLoading = false
        }
    }

    fun decreaseQuantity(shoppingCartProductItem: ShoppingCartItem.ShoppingCartProductItem) {
        if (isApiLoading) return
        isApiLoading = true
        viewModelScope.launch {
            shoppingCartRepository.updateQuantity(
                shoppingCartProductItem.shoppingCartProduct.id,
                shoppingCartProductItem.shoppingCartProduct.quantity - 1,
            ).getOrThrow()
            updateShoppingCartItems()
            _hasUpdatedProducts.value = true
            isApiLoading = false
        }
    }

    fun increaseQuantity(shoppingCartProductItem: ShoppingCartItem.ShoppingCartProductItem) {
        if (isApiLoading) return
        isApiLoading = true
        viewModelScope.launch {
            shoppingCartRepository.updateQuantity(
                shoppingCartProductItem.shoppingCartProduct.id,
                shoppingCartProductItem.shoppingCartProduct.quantity + 1,
            ).getOrThrow()
            updateShoppingCartItems()
            _hasUpdatedProducts.value = true
            isApiLoading = false
        }
    }

    fun plusPage() {
        if (!loadable) return
        page++
        loadShoppingCart()
    }

    fun selectShoppingCartProduct(
        shoppingCartProductItem: ShoppingCartItem.ShoppingCartProductItem,
        selected: Boolean,
    ) {
        _shoppingCart.value =
            _shoppingCart.value?.map { item ->
                if (item.shoppingCartProduct.id == shoppingCartProductItem.shoppingCartProduct.id) {
                    return@map shoppingCartProductItem.copy(
                        isChecked = selected,
                    )
                }
                return@map item
            }
    }

    fun selectAllShoppingCartProducts() {
        _shoppingCart.value =
            _shoppingCart.value?.map { item ->
                item.copy(
                    isChecked = true,
                )
            }
    }

    fun checkoutIfPossible() {
        if (orderBarState.value?.isOrderEnabled ?: false) {
            _orderEvent.setValue(OrderEvent.PROCEED)
        } else {
            _orderEvent.setValue(OrderEvent.ABORT)
        }
    }

    companion object {
        private const val MINIMUM_PAGE = 1
        private const val COUNT_PER_PAGE: Int = 5
    }
}
