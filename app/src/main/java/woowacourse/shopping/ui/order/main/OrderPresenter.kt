package woowacourse.shopping.ui.order.main

import woowacourse.shopping.domain.repository.OrderProductRepository
import woowacourse.shopping.domain.repository.PointRepository
import woowacourse.shopping.model.UiCartProduct
import woowacourse.shopping.model.UiPrice
import woowacourse.shopping.ui.order.main.OrderContract.Presenter
import woowacourse.shopping.ui.order.main.OrderContract.View

class OrderPresenter(
    view: View,
    private val cartProducts: List<UiCartProduct>,
    private val orderProductRepository: OrderProductRepository,
    private val pointRepository: PointRepository,
) : Presenter(view) {
    private var originalPayment = 0
    private var finalPayment = 0
    private var discountPayment = 0

    override fun loadOrderProducts() {
        view.showOrderProductList(cartProducts)
    }

    override fun loadAvailablePoints() {
        pointRepository.requestPoints(
            onSuccess = { point ->
                view.showAvailablePoint(point)
            },
            onFailure = { },
        )
    }

    override fun loadPayment() {
        originalPayment = cartProducts.sumOf { it.product.price.value * it.selectedCount.value }
        view.showTotalPayment(UiPrice(originalPayment))
    }

    override fun calculateFinalPayment(point: Int) {
        discountPayment = point
        finalPayment = originalPayment - discountPayment
        view.showFinalPayment(UiPrice(finalPayment))
    }

    override fun order() {
    }

    override fun navigateToHome(itemId: Int) {
        when (itemId) {
            android.R.id.home -> {
                view.navigateToHome()
            }
        }
    }
}
