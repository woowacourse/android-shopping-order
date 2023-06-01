package woowacourse.shopping.feature.order

import com.example.domain.repository.CartRepository
import woowacourse.shopping.mapper.toPresentation
import woowacourse.shopping.model.CartProductUiModel

class OrderPresenter(
    private val view: OrderContract.View,
    private val cartRepository: CartRepository,
) :
    OrderContract.Presenter {

    private var cartProducts: List<CartProductUiModel> = listOf()
    override fun requestProducts(cartIds: List<Long>) {
        cartRepository.getAll(
            onSuccess = { it ->
                val products = mutableListOf<CartProductUiModel>()
                cartProducts = it.map { it.toPresentation() }
                cartIds.forEach { cartId ->
                    val cartProduct: CartProductUiModel? = cartProducts.find { it.cartId == cartId }
                    if (cartProduct != null) products.add(cartProduct)
                }
                view.showProducts(products)
            },
            onFailure = {},
        )
    }
}
