package woowacourse.shopping.presentation.view.productdetail

import woowacourse.shopping.presentation.mapper.toUIModel
import woowacourse.shopping.presentation.model.ProductModel
import woowacourse.shopping.presentation.model.RecentProductModel
import woowacouse.shopping.data.repository.cart.CartRepository
import woowacouse.shopping.data.repository.product.ProductRepository

class ProductDetailPresenter(
    private val view: ProductDetailContract.View,
    productId: Long,
    productRepository: ProductRepository,
    private val cartRepository: CartRepository,
) : ProductDetailContract.Presenter {
    private lateinit var product: ProductModel

    init {
        productRepository.loadDataById(productId, ::onFailure) { product ->
            this.product = product.toUIModel()
            loadProductInfo()
        }
    }

    private fun onFailure() {
        view.handleErrorView()
    }

    override fun loadLastRecentProductInfo(recentProduct: RecentProductModel?) {
        if (recentProduct == null || recentProduct.id == UNABLE_ID) {
            view.setGoneOfLastRecentProductInfoView()
            return
        }
        view.setVisibleOfLastRecentProductInfoView(recentProduct)
    }

    override fun loadProductInfo() {
        if (product.id == UNABLE_ID) {
            view.handleErrorView()
            view.exitProductDetailView()
            return
        }
        view.setProductInfoView(product)
    }

    override fun addCart(count: Int) {
        cartRepository.loadAllCarts(::onFailure) { carts ->
            val cartProduct = carts.find { cartProduct -> cartProduct.product.id == product.id }
            if (cartProduct == null) {
                cartRepository.addCartProduct(product.id, ::onFailure) { cartId ->
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
                val newCartProduct = cart.copy(count = cart.count + count)
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
