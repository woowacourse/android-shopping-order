package woowacourse.shopping.view.shoppingCart.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.shoppingCart.repository.DefaultShoppingCartRepository
import woowacourse.shopping.data.shoppingCart.repository.ShoppingCartRepository
import woowacourse.shopping.domain.shoppingCart.ShoppingCartProduct
import woowacourse.shopping.view.shoppingCart.ShoppingCartItem

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

    private var page: Int = MINIMUM_PAGE

    private var loadable: Boolean = false

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

        viewModelScope.launch {
            val shoppingCarts = shoppingCartRepository.load(page, size)
            _isLoading.value = false
            loadable = !shoppingCarts.last

            loadShoppingCartItems(
                shoppingCarts.shoppingCartItems,
            )
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

    private fun updateShoppingCartItems() {
        viewModelScope.launch {
            val shoppingCarts =
                shoppingCartRepository.load(0, COUNT_PER_PAGE * page).shoppingCartItems
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
    }

    fun removeShoppingCartProduct(shoppingCartProductItem: ShoppingCartItem.ShoppingCartProductItem) {
        viewModelScope.launch {
            shoppingCartRepository.remove(shoppingCartProductItem.shoppingCartProduct.id)
            updateShoppingCartItems()
            _hasUpdatedProducts.postValue(true)
        }
    }

    fun decreaseQuantity(shoppingCartProductItem: ShoppingCartItem.ShoppingCartProductItem) {
        viewModelScope.launch {
            shoppingCartRepository.updateQuantity(
                shoppingCartProductItem.shoppingCartProduct.id,
                shoppingCartProductItem.shoppingCartProduct.quantity - 1,
            )
            updateShoppingCartItems()
            _hasUpdatedProducts.value = true
        }
    }

    fun increaseQuantity(shoppingCartProductItem: ShoppingCartItem.ShoppingCartProductItem) {
        viewModelScope.launch {
            shoppingCartRepository.updateQuantity(
                shoppingCartProductItem.shoppingCartProduct.id,
                shoppingCartProductItem.shoppingCartProduct.quantity + 1,
            )
            updateShoppingCartItems()
            _hasUpdatedProducts.value = true
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
