package woowacourse.shopping.feature.order.confirm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.domain.repository.CartRepository
import woowacourse.shopping.mapper.toPresentation
import woowacourse.shopping.model.CartProductUiModel

class OrderConfirmPresenter(
    private val view: OrderConfirmContract.View,
    private val cartRepository: CartRepository,
    private val cartIds: List<Long>
) : OrderConfirmContract.Presenter {
    private val _cartProducts: MutableLiveData<List<CartProductUiModel>> = MutableLiveData()
    override val cartProducts: LiveData<List<CartProductUiModel>>
        get() = _cartProducts

    override fun loadSelectedCarts() {
        cartRepository.getAll(
            onSuccess = { cartProducts ->
                val selectedCartItems = cartProducts.filter { it.cartId in cartIds }
                _cartProducts.postValue(selectedCartItems.map { it.toPresentation() })
            },
            onFailure = { }
        )
    }
}
