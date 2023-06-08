package woowacourse.shopping.view.payment

import com.shopping.domain.CartItemId
import com.shopping.domain.OrderPay
import com.shopping.repository.CartProductRepository
import com.shopping.repository.MemberRepository
import com.shopping.repository.PayRepository
import woowacourse.shopping.model.uimodel.mapper.toUIModel

class PaymentPresenter(
    private val view: PaymentContract.View,
    private val cartProductRepository: CartProductRepository,
    private val memberRepository: MemberRepository,
    private val payRepository: PayRepository
) : PaymentContract.Presenter {
    private lateinit var cartItemIds: List<Long>

    override fun updateOrderProducts(cartIds: Array<Long>) {
        cartItemIds = cartIds.toList()

        cartProductRepository.getCartItemsWithIds(
            cartItemIds,
            onSuccess = { orderProducts ->
                view.updateOrderProducts(orderProducts.map { it.toUIModel() })
                var price = 0
                orderProducts.forEach {
                    price += (it.price)
                }
                view.updateTotalPrice(price)
            }
        )
    }

    override fun payOrderProducts(originalPrice: Int, points: Int) {
        payRepository.postPay(
            OrderPay(
                cartItemIds.map { CartItemId(it) },
                originalPrice,
                points
            ),
            onSuccess = {
                view.showOrderDetail(it)
            }
        )
    }

    override fun getPoints() {
        memberRepository.getPoint {
            view.updatePoints(it.point)
        }
    }
}
