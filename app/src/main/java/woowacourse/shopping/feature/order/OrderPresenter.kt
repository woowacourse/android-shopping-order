package woowacourse.shopping.feature.order

import com.example.domain.repository.CartRepository
import woowacourse.shopping.mapper.toPresentation
import woowacourse.shopping.model.CartProductUiModel

class OrderPresenter(
    private val view: OrderContract.View,
    private val cartRepository: CartRepository,
) :
    OrderContract.Presenter {
    private var orderProducts: List<CartProductUiModel> = listOf()
    private var discountCondition: Discount.Condition? = null
    private var totalPrice = 0

    override fun requestProducts(cartIds: List<Long>) {
        cartRepository.getAll(
            onSuccess = { it ->
                val products = mutableListOf<CartProductUiModel>()
                val cartProducts = it.map { it.toPresentation() }
                cartIds.forEach { cartId ->
                    val cartProduct: CartProductUiModel? = cartProducts.find { it.cartId == cartId }
                    if (cartProduct != null) products.add(cartProduct)
                }
                orderProducts = products
                view.showProducts(orderProducts)
                calculatePrice()
            },
            onFailure = {},
        )
    }

    fun calculatePrice() {
        totalPrice = orderProducts.sumOf {
            it.productUiModel.price * it.productUiModel.count
        }
        calculateDiscountedPrice()
    }

    private fun calculateDiscountedPrice() {
        val (discountedPrice, discountCondition) = Discount(totalPrice).use()
        this.discountCondition = discountCondition
        when (this.discountCondition) {
            null -> showNonDiscount()
            else -> showDiscount(discountedPrice, discountCondition ?: return showNonDiscount())
        }
    }

    private fun showNonDiscount() {
        view.showNonDiscount()
        view.showFinalPrice(totalPrice)
    }

    private fun showDiscount(discountedPrice: Int, discountCondition: Discount.Condition) {
        view.showDiscount(discountCondition.standardPrice, discountCondition.amount)
        view.showFinalPrice(discountedPrice)
    }
}
