package woowacourse.shopping.presentation.productdetail

import woowacourse.shopping.data.cart.CartRepository
import woowacourse.shopping.model.Product
import woowacourse.shopping.presentation.mapper.toPresentation

class ProductDetailPresenter(
    private val view: ProductDetailContract.View,
    private val cartRepository: CartRepository,
) : ProductDetailContract.Presenter {

    private lateinit var currentProduct: Product

    override fun loadProductDetail(productId: Long) {
        cartRepository.findProductById(productId) { product ->
            currentProduct = product
            view.showProductDetail(product.toPresentation())
        }
    }

    override fun loadRecentProduct(productId: Long) {
        cartRepository.findProductById(productId) { recentProduct ->
            view.showRecentProduct(recentProduct.toPresentation())
        }
    }

    override fun putProductInCart(count: Int) {
        cartRepository.insertCartProduct(currentProduct.id, count) {}
    }
}
