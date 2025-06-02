package woowacourse.shopping.view.shoppingCart.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.domain.shoppingCart.ShoppingCartProduct
import woowacourse.shopping.view.shoppingCart.OrderEvent
import woowacourse.shopping.view.shoppingCart.ShoppingCartItem

class OrderBarViewModel : ViewModel() {
    private val _totalPrice: MutableLiveData<Int> = MutableLiveData<Int>().apply { value = 0 }
    val totalPrice: LiveData<Int> get() = _totalPrice

    private val _totalQuantity: MutableLiveData<Int> = MutableLiveData<Int>().apply { value = 0 }
    val totalQuantity: LiveData<Int> get() = _totalQuantity

    private val _isAllSelected: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>().apply { value = false }
    val isAllSelected: LiveData<Boolean> get() = _isAllSelected

    private val _shoppingCartProductsToOrder: MutableLiveData<List<ShoppingCartProduct>> =
        MutableLiveData(emptyList())
    val shoppingCartProductsToOrder: LiveData<List<ShoppingCartProduct>> get() = _shoppingCartProductsToOrder

    private val _isOrderEnabled: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>().apply { value = false }
    val isOrderEnabled: LiveData<Boolean> get() = _isOrderEnabled

    private val _event: MutableLiveData<OrderEvent> = MutableLiveData()
    val event: LiveData<OrderEvent> get() = _event

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

    fun checkoutIfPossible() {
        if (isOrderEnabled.value == true) {
            _event.value = OrderEvent.PROCEED
        } else {
            _event.value = OrderEvent.ABORT
        }
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
