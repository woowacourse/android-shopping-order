package woowacourse.shopping.feature.order.order

import com.example.domain.model.Discount
import com.example.domain.repository.CartRepository
import com.example.domain.repository.OrderRepository
import woowacourse.shopping.mapper.toPresentation
import woowacourse.shopping.model.CartProductUiModel

class OrderPresenter(
    private val cartIds: List<Long>,
    private val view: OrderContract.View,
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
) :
    OrderContract.Presenter {
    private var orderProducts: List<CartProductUiModel> = listOf()
    private var discountCondition: Discount.Condition? = null
    private var totalPrice = 0
    private var payAmount = 0

    override fun requestProducts() {
        cartRepository.getAll(
            onSuccess = { it ->
                val products = mutableListOf<CartProductUiModel>()
                val cartProducts = it.map { it.toPresentation() }
                cartIds.forEach { cartId ->
                    val cartProduct: CartProductUiModel? = cartProducts.find { it.cartId == cartId }
                    cartProduct?.let { products.add(it) }
                }
                orderProducts = products
                view.showProducts(orderProducts)
                calculatePrice()
            },
            onFailure = {},
        )
    }

    override fun order() {
        val productCount = orderProducts.sumOf { it.productUiModel.count }
        if (productCount <= MAXIMUM_ORDERABLE_COUNT) {
            // 서버에 주문 요청을 한다.
            orderRepository.addOrder(
                cartIds,
                payAmount,
                onSuccess = { orderId ->
                    view.succeedInOrder(orderId)
                },
                onFailure = {
                    view.failToOrder()
                },
            )
        } else {
            // view를 이용해 주문이 불가함을 알려준다.
            view.failToOrder()
        }
    }

    private fun calculatePrice() {
        totalPrice = orderProducts.sumOf {
            it.productUiModel.price * it.productUiModel.count
        }
        calculatePayAmount()
    }

    private fun calculatePayAmount() {
        calculateDiscount()
        view.showPayAmount(payAmount)
    }

    private fun calculateDiscount() {
        val (discountedPrice, discountCondition) = Discount(totalPrice).use()
        this.discountCondition = discountCondition
        when (this.discountCondition) {
            null -> {
                payAmount = totalPrice
                showNonDiscount()
            }
            else -> {
                payAmount = discountedPrice
                showDiscount(discountCondition ?: return showNonDiscount())
            }
        }
    }

    private fun showNonDiscount() {
        view.showNonDiscount()
        view.showPayAmountInfo(totalPrice)
    }

    private fun showDiscount(discountCondition: Discount.Condition) {
        view.showDiscount(discountCondition.standardPrice, discountCondition.amount)
        view.showPayAmountInfo(totalPrice, discountCondition.amount)
    }

    companion object {
        private const val MAXIMUM_ORDERABLE_COUNT = 99
    }
}
