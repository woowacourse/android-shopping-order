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

    private val _isAllSelected: MediatorLiveData<Boolean> =
        MediatorLiveData<Boolean>().apply { value = false }
    val isAllSelected: LiveData<Boolean> get() = _isAllSelected

    private var page: Int = MINIMUM_PAGE

    private var loadable: Boolean = false

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

        _isAllSelected.addSource(_shoppingCart) {
            _isAllSelected.value = isAllSelected(it)
        }
    }

    private fun isAllSelected(items: List<ShoppingCartItem>): Boolean {
        val shoppingCartProductItems = items.filterIsInstance<ShoppingCartProductItem>()
        return if (shoppingCartProductItems.isEmpty()) {
            false
        } else {
            shoppingCartProductItems
                .all { item -> item.isChecked }
        }
    }

    fun updateShoppingCart() {
        val size = COUNT_PER_PAGE + 1
        val page = this.page - 1
        shoppingCartRepository.load(page, size) { result ->
            result
                .onSuccess { shoppingCartProducts: List<ShoppingCartProduct> ->
                    if (isEmptyPage(shoppingCartProducts)) return@load

                    updateShoppingCartItems(
                        shoppingCartProducts,
                        shoppingCart.value ?: emptyList(),
                    )

                    loadable = shoppingCartProducts.size == COUNT_PER_PAGE + 1

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

    private fun updateShoppingCartItems(
        products: List<ShoppingCartProduct>,
        currentShoppingCartItems: List<ShoppingCartItem>,
    ) {
        val shoppingCartProductsToShow = products.take(COUNT_PER_PAGE)

        if (currentShoppingCartItems.isEmpty() || page != 1) {
            _shoppingCart.value =
                buildList {
                    addAll(currentShoppingCartItems)
                    addAll(products.map(::ShoppingCartProductItem))
                }
        } else {
            val productItems =
                shoppingCartProductsToShow.map { product ->
                    val existing =
                        currentShoppingCartItems
                            .filterIsInstance<ShoppingCartProductItem>()
                            .find { it.shoppingCartProduct.id == product.id }

                    ShoppingCartProductItem(
                        shoppingCartProduct = product,
                        isChecked = existing?.isChecked == true,
                    )
                }
            _shoppingCart.value = productItems
        }
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
        if (loadable == false) return
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

    fun selectAllShoppingCartProducts(isChecked: Boolean) {
        _shoppingCart.value =
            _shoppingCart.value?.filterIsInstance<ShoppingCartProductItem>()?.map { item ->
                item.copy(
                    isChecked = isChecked,
                )
            }
    }

    companion object {
        private const val MINIMUM_PAGE = 1
        private const val COUNT_PER_PAGE: Int = 5
    }
}
