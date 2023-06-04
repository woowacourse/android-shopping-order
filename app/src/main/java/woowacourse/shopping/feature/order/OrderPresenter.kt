package woowacourse.shopping.feature.order

import com.example.domain.Cart
import com.example.domain.repository.OrderRepository
import woowacourse.shopping.model.mapper.toUi

class OrderPresenter(
    private val view: OrderContract.View,
    private val orderProducts: Cart,
    private val orderRepository: OrderRepository
) : OrderContract.Presenter {
    private var finalPrice = 0

    override fun loadOrderProducts() {
        view.setOrderProducts(orderProducts.toUi())
    }

    override fun calculatePrice() {
        orderRepository.requestFetchDiscountPolicy(
            onSuccess = { fixedDiscountPolicies ->
                val productsSum = orderProducts.getPickedProductsTotalPrice()
                val discountPrice = fixedDiscountPolicies.getDiscountPrice(productsSum)
                finalPrice = fixedDiscountPolicies.getFinalPrice(productsSum)

                view.setProductsSum(productsSum)
                view.setDiscountPrice(discountPrice)
                view.setFinalPrice(finalPrice)
            },
            onFailure = {}
        )
    }

    override fun navigateToOrderDetail() {
        orderRepository.requestAddOrder(
            cartIds = orderProducts.products.map { it.id },
            finalPrice = finalPrice,
            onSuccess = { orderId: Long ->
                view.showOrderDetailPage(orderId)
            },
            onFailure = {}
        )
    }
}
