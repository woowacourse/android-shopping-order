package woowacourse.shopping.presentation.view.order

import com.example.domain.cart.CartProducts
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.data.mapper.toUiModel
import woowacourse.shopping.data.respository.point.PointRepository
import woowacourse.shopping.presentation.model.CartModel

class OrderPresenter(
    private val view: OrderContract.View,
    cartItems: List<CartModel>,
    private val pointRepository: PointRepository,
) : OrderContract.Presenter {
    private val cartItemsDomain = CartProducts(cartItems.map { it.toDomain() })

    override fun initReservedPoint() {
        pointRepository.requestReservedPoint(::onFailure) { pointEntity ->
            view.setAvailablePointView(pointEntity.point)
        }
    }

    override fun initSavingPoint() {
        pointRepository.requestSavingPoint(
            cartItemsDomain.totalCheckedPrice,
            ::onFailure,
        ) { savingPointEntity ->
            view.setSavingPoint(savingPointEntity.savingPoint)
        }
    }

    override fun initCartProducts() {
        view.setCartProductsView(cartItemsDomain.all.map { it.toUiModel() })
    }

    override fun initView() {
        view.setTotalPriceView(cartItemsDomain.totalCheckedPrice)
    }

    fun onFailure() {
        view.handleErrorView()
    }
}
