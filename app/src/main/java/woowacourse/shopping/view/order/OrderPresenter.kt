package woowacourse.shopping.view.order

import woowacourse.shopping.domain.model.OrderCartItemsDTO
import woowacourse.shopping.domain.repository.MypageRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.model.OrderCartProductsModel
import woowacourse.shopping.model.OrderModel

class OrderPresenter(
    private val view: OrderContract.View,
    private val products: OrderCartProductsModel,
    private val orderRepository: OrderRepository,
    private val mypageRepository: MypageRepository,
) : OrderContract.Presenter {
    private val totalPrice = products.orderProducts.sumOf { it.price * it.quantity }
    private var ownCash = 0

    override fun fetchOrder() {
        mypageRepository.getCash {
            ownCash = it
            view.showOrder(OrderModel(products, it, it - totalPrice, totalPrice))
        }
    }

    override fun order() {
        if (ownCash - totalPrice < 0) {
            view.showUnableToast()
            return
        }
        val orderCartItemsDTO = products.toDTO()
        orderRepository.order(orderCartItemsDTO) {
            if (it != null) {
                view.showOrderComplete(it)
            }
        }
    }

    private fun OrderCartProductsModel.toDTO(): OrderCartItemsDTO {
        return OrderCartItemsDTO(
            orderProducts.map {
                OrderCartItemsDTO.OrderCartItemDTO(
                    it.cartId,
                    it.name,
                    it.price,
                    it.imageUrl,
                )
            },
        )
    }
}
