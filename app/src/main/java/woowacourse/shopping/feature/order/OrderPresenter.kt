package woowacourse.shopping.feature.order

import com.example.domain.Cart
import com.example.domain.repository.OrderRepository

class OrderPresenter(
    private val view: OrderContract.View,
    private val orderProducts: Cart,
    private val orderRepository: OrderRepository
) : OrderContract.Presenter {

    override fun loadOrderProducts() {
        view.setOrderProducts(orderProducts)
    }

    override fun calculatePrice() {
        view.setProductsSum(orderProducts.getPickedProductsTotalPrice())
    }
}
