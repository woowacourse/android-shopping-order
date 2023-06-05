package woowacourse.shopping.view.payment

import woowacourse.shopping.model.data.dto.CartItemIdDTO
import woowacourse.shopping.model.data.dto.CartProductDTO
import woowacourse.shopping.model.data.dto.OrderPayDTO
import woowacourse.shopping.model.uimodel.OrderProductUIModel
import woowacourse.shopping.server.retrofit.CartItemsService
import woowacourse.shopping.server.retrofit.MembersService
import woowacourse.shopping.server.retrofit.PayService
import woowacourse.shopping.server.retrofit.createResponseCallback

class PaymentPresenter(
    private val view: PaymentContract.View,
    private val cartItemService: CartItemsService,
    private val membersService: MembersService,
    private val payService: PayService
) : PaymentContract.Presenter {
    private lateinit var cartItemIds: List<Long>

    override fun updateOrderProducts(cartIds: Array<Long>) {
        cartItemIds = cartIds.toList()
        val orderProducts = mutableListOf<OrderProductUIModel>()

        cartItemService.getCartItems().enqueue(
            createResponseCallback(
                onSuccess = { cartProducts ->
                    cartIds.forEach { cartId ->
                        val orderProduct = cartProducts.find { cartProduct ->
                            cartProduct.id == cartId
                        } ?: throw IllegalStateException("해당 상품을 찾을 수 없습니다.")
                        orderProducts.add(orderProduct.transformToOrderProduct())
                    }
                    view.updateOrderProducts(orderProducts)
                    var price = 0
                    orderProducts.forEach {
                        price += (it.price)
                    }
                    view.updateTotalPrice(price)
                },
                onFailure = {
                    throw IllegalStateException("상품을 불러오는데 실패했습니다.")
                }
            )
        )
    }

    override fun payOrderProducts(originalPrice: Int, points: Int) {
        payService.postPay(OrderPayDTO(cartItemIds.map { CartItemIdDTO(it) }, originalPrice, points)).enqueue(
            createResponseCallback(
                onSuccess = { received ->
                    view.showOrderDetail(received.orderId)
                },
                onFailure = {
                    throw IllegalStateException("서버 통신(상품 목록)에 실패했습니다.")
                }
            )
        )
        }

        override fun getPoints() {
            membersService.getPoint().enqueue(
                createResponseCallback(
                    onSuccess = { received ->
                        view.updatePoints(received.point)
                    },
                    onFailure = {
                        throw IllegalStateException("서버 통신(포인트)에 실패했습니다.")
                    }
                )
            )
        }

        private fun CartProductDTO.transformToOrderProduct(): OrderProductUIModel =
            OrderProductUIModel(product.name, product.imageUrl, quantity, product.price * quantity)
    }
    