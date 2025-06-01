package woowacourse.shopping.view.shoppingCart.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.domain.shoppingCart.ShoppingCartProduct
import woowacourse.shopping.view.shoppingCart.ShoppingCartItem

class OrderBarViewModel : ViewModel() {

    private val _totalPrice: MediatorLiveData<Int> = MediatorLiveData<Int>().apply { value = 0 }
    val totalPrice: LiveData<Int> get() = _totalPrice

    private val _totalQuantity: MediatorLiveData<Int> = MediatorLiveData<Int>().apply { value = 0 }
    val totalQuantity: LiveData<Int> get() = _totalQuantity

    private val _isAllSelected: MediatorLiveData<Boolean> =
        MediatorLiveData<Boolean>().apply { value = false }
    val isAllSelected: LiveData<Boolean> get() = _isAllSelected

    private val _shoppingCartProductsToOrder: MediatorLiveData<List<ShoppingCartProduct>> =
        MediatorLiveData(emptyList())
    val shoppingCartProductsToOrder: LiveData<List<ShoppingCartProduct>> get() = _shoppingCartProductsToOrder

    private val _isOrderEnabled: MediatorLiveData<Boolean> =
        MediatorLiveData<Boolean>().apply { value = false }
    val isOrderEnabled: LiveData<Boolean> get() = _isOrderEnabled

    fun update(shoppingCart: List<ShoppingCartItem.ShoppingCartProductItem>) {
        _totalPrice.value =
            shoppingCart
                .filter { item -> item.isChecked }
                .sumOf { item -> item.shoppingCartProduct.price }
        _totalQuantity.value =
            shoppingCart
                .filter { item -> item.isChecked }
                .sumOf { item -> item.shoppingCartProduct.quantity }

        _isAllSelected.value = isAllSelected(shoppingCart)

        _shoppingCartProductsToOrder.value =
            shoppingCart
                .filter { it.isChecked }
                .map { it.shoppingCartProduct }

        _isOrderEnabled.value = totalQuantity.value?.let { it > 0 } ?: false

    }

    private fun isAllSelected(items: List<ShoppingCartItem>): Boolean {
        val shoppingCartProductItems =
            items.filterIsInstance<ShoppingCartItem.ShoppingCartProductItem>()
        return if (shoppingCartProductItems.isEmpty()) {
            false
        } else {
            shoppingCartProductItems
                .all { item -> item.isChecked }
        }
    }

}
