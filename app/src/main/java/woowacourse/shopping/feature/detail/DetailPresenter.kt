package woowacourse.shopping.feature.detail

import com.example.domain.repository.CartRepository
import woowacourse.shopping.mapper.toDomain
import woowacourse.shopping.model.ProductUiModel

class DetailPresenter(
    private val view: DetailContract.View,
    private val cartRepository: CartRepository,
    product: ProductUiModel
) : DetailContract.Presenter {
    private var _product: ProductUiModel = product
    override val product: ProductUiModel
        get() = _product

    private var _count: Int = 1
    override val count get() = _count

    override fun increaseCount() {
        _count++
    }

    override fun decreaseCount() {
        _count--
    }

    override fun selectCount() {
        view.showSelectCountScreen(product)
    }

    override fun addCart() {
        val cartProduct = cartRepository.getCartProductByProduct(product = product.toDomain())
        if (cartProduct == null) {
            val cartItemId = cartRepository.addProduct(_product.toDomain())
            cartRepository.updateProduct(cartItemId, _count)
        } else {
            cartRepository.updateProduct(
                cartProduct.cartProductId.toInt(),
                cartProduct.count + _count
            )
        }

        view.showCartScreen()
    }
}
