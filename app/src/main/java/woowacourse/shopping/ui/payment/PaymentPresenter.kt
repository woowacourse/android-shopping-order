package woowacourse.shopping.ui.payment

import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.UserRepository
import woowacourse.shopping.ui.mapper.toUserUiModel
import woowacourse.shopping.ui.model.BasketProductUiModel

class PaymentPresenter(
    private val view: PaymentContract.View,
    private val basketProducts: List<BasketProductUiModel>,
    private val totalPrice: Int,
    private val userRepository: UserRepository,
    private val orderRepository: OrderRepository,
) : PaymentContract.Presenter {

    override fun getUser() {
        userRepository.getUser {
            view.initView(
                user = it.toUserUiModel(),
                basketProducts = basketProducts,
                totalPrice = totalPrice
            )
        }
    }

    override fun addOrder(usingPoint: Int) {
        orderRepository.addOrder(
            basketIds = basketProducts.map { it.id },
            usingPoint = usingPoint,
            totalPrice = basketProducts.sumOf { it.product.price.value * it.count.value },
            onAdded = view::showOrderDetail,
            onFailed = view::showOrderFailedMessage
        )
    }
}
