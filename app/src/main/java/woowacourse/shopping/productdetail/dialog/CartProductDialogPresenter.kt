package woowacourse.shopping.productdetail.dialog

import woowacourse.shopping.common.model.ProductModel
import woowacourse.shopping.common.model.mapper.ProductMapper.toDomain
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.repository.CartRepository

class CartProductDialogPresenter(
    private val view: CartProductDialogContract.View,
    productModel: ProductModel,
    private val cartRepository: CartRepository,
    cartProductAmount: Int
) : CartProductDialogContract.Presenter {
    private var cartProduct: CartProduct

    init {
        cartProduct = CartProduct(-1, cartProductAmount, true, productModel.toDomain())
        updateCartProductAmount()
    }

    override fun decreaseCartProductAmount() {
        if (cartProduct.quantity > MINIMUM_CART_PRODUCT_AMOUNT) {
            cartProduct = cartProduct.decreaseAmount()
            updateCartProductAmount()
        }
    }

    override fun increaseCartProductAmount() {
        cartProduct = cartProduct.increaseAmount()
        updateCartProductAmount()
    }

    private fun updateCartProductAmount() {
        view.updateCartProductAmount(cartProduct.quantity)
    }

    override fun addToCart() {
        cartRepository.findId(
            productId = cartProduct.product.id,
            onSuccess = {
                if (it == null) {
                    addCartProduct()
                } else {
                    val cartProduct = cartProduct.copy(id = it)
                    updateCartProduct(cartProduct)
                    view.notifyAddToCartCompleted()
                }
            },
            onFailure = {}
        )
    }

    private fun addCartProduct() {
        cartRepository.addCartProduct(
            product = cartProduct.product,
            onSuccess = {
                view.notifyAddToCartCompleted()
            },
            onFailure = {}
        )
    }

    private fun updateCartProduct(prevCartProduct: CartProduct) {
        cartProduct = prevCartProduct.copy(quantity = prevCartProduct.quantity + cartProduct.quantity)
        cartRepository.modifyCartProduct(cartProduct)
    }

    companion object {
        private const val MINIMUM_CART_PRODUCT_AMOUNT = 1
    }
}
