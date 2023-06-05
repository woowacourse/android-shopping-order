package woowacourse.shopping.view.order

import woowacourse.shopping.data.remote.dto.OrderCartItemsDTO
import woowacourse.shopping.data.remote.result.DataResult
import woowacourse.shopping.data.repository.MypageRepository
import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.model.OrderCartProductsModel
import woowacourse.shopping.model.OrderUserInfoModel

class OrderPresenter(
    private val view: OrderContract.View,
    private val products: OrderCartProductsModel,
    private val orderRepository: OrderRepository,
    private val mypageRepository: MypageRepository,
) : OrderContract.Presenter {
    private val totalPrice = products.orderProducts.sumOf { it.price * it.quantity }
    private var ownCash = 0

    override fun fetchOrder() {
        mypageRepository.getCash { result ->
            when (result) {
                is DataResult.Success -> {
                    ownCash = result.response
                    view.showOrder(OrderUserInfoModel(products, ownCash, ownCash - totalPrice, totalPrice))
                }
                is DataResult.Failure -> {
                    view.showErrorMessageToast(result.message)
                }
            }
        }
    }

    override fun order() {
        if (ownCash - totalPrice < 0) {
            view.showUnableToast()
            return
        }
        val orderCartItemsDTO = products.toDTO()
        orderRepository.order(orderCartItemsDTO) { result ->
            when (result) {
                is DataResult.Success -> {
                    if (result.response != null) {
                        view.showOrderComplete(result.response)
                    }
                }
                is DataResult.Failure -> {
                    view.showErrorMessageToast(result.message)
                }
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
