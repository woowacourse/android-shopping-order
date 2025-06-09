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
import woowacourse.shopping.domain.shoppingCart.ShoppingCarts
import woowacourse.shopping.view.common.MutableSingleLiveData
import woowacourse.shopping.view.common.SingleLiveData

class ShoppingCartViewModel(
    private val shoppingCartRepository: ShoppingCartRepository = DefaultShoppingCartRepository.get(),
) : ViewModel() {
    private val _shoppingCart: MutableLiveData<List<ShoppingCartItem>> = MutableLiveData()
    val shoppingCart: LiveData<List<ShoppingCartItem>> get() = _shoppingCart

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

    var hasUpdatedProducts: Boolean = false
        private set

    init {
        _totalPrice.addSource(_shoppingCart) { it ->
            _totalPrice.value =
                it
                    .filterIsInstance<ShoppingCartItem>()
                    .filter { item -> item.isChecked }
                    .sumOf { item -> item.shoppingCartProduct.price }
        }

        _totalQuantity.addSource(_shoppingCart) {
            _totalQuantity.value =
                it
                    .filterIsInstance<ShoppingCartItem>()
                    .filter { item -> item.isChecked }
                    .sumOf { item -> item.shoppingCartProduct.quantity }
        }

        _isAllSelected.addSource(_shoppingCart) {
            _isAllSelected.value = isAllSelected(it)
        }

        _shoppingCartProductsToOrder.addSource(_shoppingCart) {
            _shoppingCartProductsToOrder.value =
                it
                    .filterIsInstance<ShoppingCartItem>()
                    .filter { it.isChecked == true }
                    .map { it.shoppingCartProduct }
        }

        _isOrderEnabled.addSource(totalQuantity) {
            _isOrderEnabled.value = it > 0
        }
    }

    private fun isAllSelected(items: List<ShoppingCartItem>): Boolean =
        if (items.isEmpty()) {
            false
        } else {
            items.all { item -> item.isChecked }
        }

    fun updateShoppingCart() {
        val page = this.page - 1
        val size = COUNT_PER_PAGE
        viewModelScope.launch {
            shoppingCartRepository
                .load(page, size)
                .onSuccess { shoppingCarts: ShoppingCarts ->
                    _isLoading.value = false
                    loadable = !shoppingCarts.last

                    updateShoppingCartItems(
                        shoppingCarts.shoppingCartItems,
                        shoppingCart.value ?: emptyList(),
                    )
                }.onFailure {
                    _event.setValue(ShoppingCartEvent.UPDATE_SHOPPING_CART_FAILURE)
                    _isLoading.value = false
                }
        }
    }

    private fun updateShoppingCartItems(
        updateShoppingCarts: List<ShoppingCartProduct>,
        currentShoppingCartItems: List<ShoppingCartItem>,
    ) {
        if (currentShoppingCartItems.isEmpty()) {
            updateEmptyShoppingCart(currentShoppingCartItems, updateShoppingCarts)
            return
        }
        if (page == 1) {
            updateRefreshShoppingCart(updateShoppingCarts, currentShoppingCartItems)
            return
        }
        updateChangingShoppingCart(currentShoppingCartItems, updateShoppingCarts)
    }

    private fun updateEmptyShoppingCart(
        currentShoppingCartItems: List<ShoppingCartItem>,
        updateShoppingCarts: List<ShoppingCartProduct>,
    ) {
        _shoppingCart.value =
            buildList {
                addAll(currentShoppingCartItems)
                addAll(updateShoppingCarts.map(::ShoppingCartItem))
            }
    }

    private fun updateRefreshShoppingCart(
        updateShoppingCarts: List<ShoppingCartProduct>,
        currentShoppingCartItems: List<ShoppingCartItem>,
    ) {
        val productItems =
            updateShoppingCarts.map { product ->
                val existing =
                    currentShoppingCartItems.find { it.shoppingCartProduct.id == product.id }

                ShoppingCartItem(
                    shoppingCartProduct = product,
                    isChecked = existing?.isChecked == true,
                )
            }
        _shoppingCart.value = productItems
    }

    private fun updateChangingShoppingCart(
        currentShoppingCartItems: List<ShoppingCartItem>,
        updateShoppingCarts: List<ShoppingCartProduct>,
    ) {
        val updatedItems =
            currentShoppingCartItems
                .map { item ->
                    val updatedProduct =
                        updateShoppingCarts.find { it.id == item.shoppingCartProduct.id }
                    if (updatedProduct != null) {
                        item.copy(
                            shoppingCartProduct = updatedProduct,
                            isChecked = item.isChecked,
                        )
                    } else {
                        item
                    }
                }.toMutableList()

        val existingIds = updatedItems.map { it.shoppingCartProduct.id }

        val newItems =
            updateShoppingCarts
                .filter { it.id !in existingIds }
                .map { ShoppingCartItem(shoppingCartProduct = it) }

        updatedItems.addAll(newItems)
        _shoppingCart.value = updatedItems
    }

    fun removeShoppingCartProduct(shoppingCartProductItem: ShoppingCartItem) {
        viewModelScope.launch {
            shoppingCartRepository
                .remove(shoppingCartProductItem.shoppingCartProduct.id)
                .onSuccess {
                    removeShoppingCartItem(shoppingCartProductItem)
                    hasUpdatedProducts = true
                }.onFailure {
                    _event.setValue(ShoppingCartEvent.REMOVE_SHOPPING_CART_PRODUCT_FAILURE)
                }
        }
    }

    private fun removeShoppingCartItem(shoppingCartProductItem: ShoppingCartItem) {
        val updatedList =
            shoppingCart.value
                .orEmpty()
                .filterNot {
                    it.shoppingCartProduct.id == shoppingCartProductItem.shoppingCartProduct.id
                }

        _shoppingCart.value = updatedList
    }

    fun decreaseQuantity(shoppingCartProductItem: ShoppingCartItem) {
        viewModelScope.launch {
            val shoppingCartId = shoppingCartProductItem.shoppingCartProduct.id
            val quantityToUpdate = shoppingCartProductItem.shoppingCartProduct.quantity - 1

            if (quantityToUpdate == 0) {
                return@launch removeShoppingCartProduct(shoppingCartProductItem)
            }
            shoppingCartRepository
                .decreaseQuantity(
                    shoppingCartId,
                    quantityToUpdate,
                ).onSuccess {
                    _shoppingCart.value =
                        updateQuantity(
                            shoppingCartId,
                            quantityToUpdate,
                            shoppingCart.value.orEmpty(),
                        )
                    hasUpdatedProducts = true
                }.onFailure {
                    _event.setValue(ShoppingCartEvent.DECREASE_SHOPPING_CART_PRODUCT_FAILURE)
                }
        }
    }

    fun increaseQuantity(shoppingCartProductItem: ShoppingCartItem) {
        viewModelScope.launch {
            val shoppingCartId = shoppingCartProductItem.shoppingCartProduct.id
            val quantityToUpdate = shoppingCartProductItem.shoppingCartProduct.quantity + 1
            shoppingCartRepository
                .increaseQuantity(
                    shoppingCartId,
                    quantityToUpdate,
                ).onSuccess {
                    _shoppingCart.value =
                        updateQuantity(
                            shoppingCartId,
                            quantityToUpdate,
                            shoppingCart.value.orEmpty(),
                        )
                    hasUpdatedProducts = true
                }.onFailure {
                    _event.setValue(ShoppingCartEvent.ADD_SHOPPING_CART_PRODUCT_FAILURE)
                }
        }
    }

    private fun updateQuantity(
        targetId: Long,
        newQuantity: Int,
        currentShoppingCarts: List<ShoppingCartItem>,
    ): List<ShoppingCartItem> =
        currentShoppingCarts.map {
            if (it.shoppingCartProduct.id == targetId) {
                it.copy(shoppingCartProduct = it.shoppingCartProduct.copy(quantity = newQuantity))
            } else {
                it
            }
        }

    fun plusPage() {
        if (loadable == false) return
        page++
        updateShoppingCart()
    }

    fun selectShoppingCartProduct(
        shoppingCartProductItem: ShoppingCartItem,
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

    fun selectAllShoppingCartProducts(isChecked: Boolean) {
        _shoppingCart.value =
            _shoppingCart.value?.map { item ->
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
