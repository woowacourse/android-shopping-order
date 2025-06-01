package woowacourse.shopping.view.shoppingCart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
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

    private val _shoppingCartProductsToOrder: MediatorLiveData<List<ShoppingCartProduct>> =
        MediatorLiveData(emptyList<ShoppingCartProduct>())
    val shoppingCartProductsToOrder: LiveData<List<ShoppingCartProduct>> get() = _shoppingCartProductsToOrder

    private val _isOrderEnabled: MediatorLiveData<Boolean> =
        MediatorLiveData<Boolean>().apply { value = false }
    val isOrderEnabled: LiveData<Boolean> get() = _isOrderEnabled

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

        _shoppingCartProductsToOrder.addSource(_shoppingCart) {
            _shoppingCartProductsToOrder.value =
                it
                    .filterIsInstance<ShoppingCartProductItem>()
                    .filter { it.isChecked == true }
                    .map { it.shoppingCartProduct }
        }

        _isOrderEnabled.addSource(totalQuantity) {
            _isOrderEnabled.value = it > 0
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
        val page = this.page - 1
        val size = COUNT_PER_PAGE

        viewModelScope.launch {
            val shoppingCarts = shoppingCartRepository.load(page, size)
            _isLoading.value = false
            if (isEmptyPage(shoppingCarts.shoppingCartItems)) return@launch

            loadable = !shoppingCarts.last

            updateShoppingCartItems(
                shoppingCarts.shoppingCartItems,
                shoppingCart.value ?: emptyList(),
            )
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
        if (currentShoppingCartItems.isEmpty()) {
            _shoppingCart.value =
                buildList {
                    addAll(currentShoppingCartItems)
                    addAll(products.map(::ShoppingCartProductItem))
                }
            return
        }
        if (page == 1) {
            val productItems =
                products.map { product ->
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
            return
        }
        val updatedItems =
            currentShoppingCartItems
                .map { item ->
                    if (item is ShoppingCartProductItem) {
                        val updatedProduct =
                            products.find { it.id == item.shoppingCartProduct.id }
                        if (updatedProduct != null) {
                            item.copy(
                                shoppingCartProduct = updatedProduct,
                                isChecked = item.isChecked,
                            )
                        } else {
                            item
                        }
                    } else {
                        item
                    }
                }.toMutableList()

        val existingIds =
            updatedItems
                .filterIsInstance<ShoppingCartProductItem>()
                .map { it.shoppingCartProduct.id }

        val newItems =
            products
                .filter { it.id !in existingIds }
                .map { ShoppingCartProductItem(shoppingCartProduct = it) }

        updatedItems.addAll(newItems)

        _shoppingCart.value = updatedItems
    }

    fun removeShoppingCartProduct(shoppingCartProductItem: ShoppingCartProductItem) {
        viewModelScope.launch {
            shoppingCartRepository.remove(shoppingCartProductItem.shoppingCartProduct.id)
            updateShoppingCart()
            _hasUpdatedProducts.postValue(true)
        }
    }

    fun decreaseQuantity(shoppingCartProductItem: ShoppingCartProductItem) {
        viewModelScope.launch {
            shoppingCartRepository.updateQuantity(
                shoppingCartProductItem.shoppingCartProduct.id,
                shoppingCartProductItem.shoppingCartProduct.quantity - 1,
            )
            updateShoppingCart()
            _hasUpdatedProducts.value = true
        }
    }

    fun increaseQuantity(shoppingCartProductItem: ShoppingCartProductItem) {
        viewModelScope.launch {
            shoppingCartRepository.updateQuantity(
                shoppingCartProductItem.shoppingCartProduct.id,
                shoppingCartProductItem.shoppingCartProduct.quantity + 1,
            )
            updateShoppingCart()
            _hasUpdatedProducts.value = true
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
        private const val COUNT_PER_PAGE: Int = Int.MAX_VALUE
    }
}
