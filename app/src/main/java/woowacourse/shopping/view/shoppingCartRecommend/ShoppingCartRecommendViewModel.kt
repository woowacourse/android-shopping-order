package woowacourse.shopping.view.shoppingCartRecommend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.shoppingCart.repository.DefaultShoppingCartRepository
import woowacourse.shopping.data.shoppingCart.repository.ShoppingCartRepository
import woowacourse.shopping.domain.shoppingCart.ShoppingCartProduct

class ShoppingCartRecommendViewModel(
    private val shoppingCartRepository: ShoppingCartRepository = DefaultShoppingCartRepository.get(),
) : ViewModel() {
    private val _shoppingCartProductsToOrder: MutableLiveData<List<ShoppingCartProduct>> =
        MutableLiveData()
    val shoppingCartProductsToOrder: LiveData<List<ShoppingCartProduct>> get() = _shoppingCartProductsToOrder

    private val _totalPrice: MediatorLiveData<Int> = MediatorLiveData<Int>().apply { value = 0 }
    val totalPrice: LiveData<Int> get() = _totalPrice

    private val _totalQuantity: MediatorLiveData<Int> = MediatorLiveData<Int>().apply { value = 0 }
    val totalQuantity: LiveData<Int> get() = _totalQuantity

    init {
        _totalPrice.addSource(_shoppingCartProductsToOrder) { it ->
            _totalPrice.value =
                it
                    .sumOf { item -> item.price }
        }

        _totalQuantity.addSource(_shoppingCartProductsToOrder) {
            _totalQuantity.value =
                it
                    .sumOf { item -> item.quantity }
        }
    }

    fun updateShoppingCartProductsToOrder(shoppingCartProductsToOrder: List<ShoppingCartProduct>) {
        _shoppingCartProductsToOrder.value = shoppingCartProductsToOrder
    }
}
