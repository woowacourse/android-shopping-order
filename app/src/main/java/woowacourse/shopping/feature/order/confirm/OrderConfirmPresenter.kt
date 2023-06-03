package woowacourse.shopping.feature.order.confirm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.domain.model.CartProduct
import com.example.domain.model.MoneySalePolicy
import com.example.domain.repository.CartRepository
import com.example.domain.repository.OrderRepository
import woowacourse.shopping.mapper.toPresentation
import woowacourse.shopping.model.CartProductUiModel

class OrderConfirmPresenter(
    private val view: OrderConfirmContract.View,
    private val moneySalePolicy: MoneySalePolicy,
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val cartIds: List<Long>
) : OrderConfirmContract.Presenter {
    private val _cartProducts: MutableLiveData<List<CartProductUiModel>> = MutableLiveData()
    override val cartProducts: LiveData<List<CartProductUiModel>>
        get() = _cartProducts

    private var paymentPrice: Int = 0

    override fun loadSelectedCarts() {
        cartRepository.getAll(
            onSuccess = { cartProducts ->
                val selectedCartItems = cartProducts.filter { it.cartId in cartIds }
                _cartProducts.postValue(selectedCartItems.map { it.toPresentation() })
                payInfo(selectedCartItems)
            },
            onFailure = { }
        )
    }

    override fun requestOrder() {
        orderRepository.addOrder(
            cartIds,
            paymentPrice,
            onSuccess = {
                view.showOrderSuccess(cartIds)
                view.exitScreen()
            },
            onFailure = { view.showOrderFailed() }
        )
    }

    private fun payInfo(cartProducts: List<CartProduct>) {
        val originPrice = cartProducts.sumOf { it.count * it.product.price.value }
        val paymentInfo = moneySalePolicy.saleApply(cartProducts)
        val saleInfo = paymentInfo.first.toPresentation()
        paymentPrice = paymentInfo.second.value
        when (saleInfo.saleAmount) {
            ZERO_MONEY -> view.showNoneSaleInfo()
            else -> {
                view.showSaleInfo()
                view.setSaleInfo(saleInfo)
            }
        }
        view.setPayInfo(originPrice, originPrice - paymentPrice)
        view.setFinalPayInfo(paymentPrice)
    }

    companion object {
        private const val ZERO_MONEY = "0"
    }
}
