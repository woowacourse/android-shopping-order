package woowacourse.shopping.presentation.view.productdetail

import woowacourse.shopping.presentation.mapper.toUIModel
import woowacourse.shopping.presentation.model.ProductModel
import woowacourse.shopping.presentation.model.RecentProductModel
import woowacouse.shopping.data.repository.cart.CartRepository
import woowacouse.shopping.data.repository.product.ProductRepository

class ProductDetailPresenter(
    private val view: ProductDetailContract.View,
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
) : ProductDetailContract.Presenter {
    private lateinit var product: ProductModel

    override fun setProduct(productId: Long) {
        productRepository.loadDataById(productId, ::onFailure) { product ->
            this.product = product.toUIModel()
            loadProductInfo()
        }
    }

    private fun onFailure(throwable: Throwable) {
        throwable.message?.let { view.handleErrorView(it) }
    }

    private fun onFailure(message: String) {
        view.handleErrorView(message)
    }

    private fun loadProductInfo() {
        if (product.id == UNABLE_ID) {
            view.handleErrorView("")
            view.exitProductDetailView()
            return
        }
        view.showProductInfoView(product)
    }

    override fun loadLastRecentProductInfo(recentProduct: RecentProductModel?) {
        if (recentProduct == null || recentProduct.id == UNABLE_ID) {
            view.hideLastRecentProductInfoView()
            return
        }
        view.showVisibleOfLastRecentProductInfoView(recentProduct)
    }

    override fun addCart(productId: Long, count: Int) {
        cartRepository.loadAllCarts(::onFailure) { carts ->
            val cartProduct = carts.find { cartProduct -> cartProduct.product.id == productId }
            if (cartProduct == null) {
                cartRepository.addCartProduct(productId, ::onFailure) { cartId ->
                    cartRepository.addLocalCart(cartId)

                    view.addCartSuccessView()
                    view.exitProductDetailView()

                    if (count > UPDATE_COUNT_CONDITION) {
                        cartRepository.loadAllCarts(::onFailure) { reLoadCarts ->
                            reLoadCarts.find { cartProduct -> cartProduct.id == cartId }?.let {
                                val newCartProduct = it.copy(count = count)
                                cartRepository.updateCartCount(newCartProduct, ::onFailure) {
                                    view.addCartSuccessView()
                                    view.exitProductDetailView()
                                }
                            }
                        }
                    }
                }
            }

            cartProduct?.let { cart ->
                val newCartProduct = cart.updateCount(cart.count + count)
                cartRepository.updateCartCount(newCartProduct, ::onFailure) {
                    view.addCartSuccessView()
                    view.exitProductDetailView()
                }
            }
        }
    }

    override fun showCount() {
        view.showCountView(product)
    }

    companion object {
        private const val UNABLE_ID = -1L
        private const val UPDATE_COUNT_CONDITION = 1
    }
}
