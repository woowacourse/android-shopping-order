package woowacourse.shopping.presentation.view.productdetail

import woowacourse.shopping.data.mapper.toUIModel
import woowacourse.shopping.data.respository.cart.CartRepository
import woowacourse.shopping.data.respository.product.ProductRepository
import woowacourse.shopping.presentation.model.CartModel
import woowacourse.shopping.presentation.model.ProductModel
import woowacourse.shopping.presentation.model.RecentProductModel

class ProductDetailPresenter(
    private val view: ProductDetailContract.View,
    productId: Long,
    productRepository: ProductRepository,
    private val cartRepository: CartRepository,
) : ProductDetailContract.Presenter {
    private val carts = mutableListOf<CartModel>()
    private lateinit var product: ProductModel

    init {
        productRepository.loadDataById(productId, ::onFailure) { productEntity ->
            product = productEntity.toUIModel()
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
            this.carts.clear()
            this.carts.addAll(carts.map { it.toUIModel() })

            val cartProduct = carts.find { cartProduct -> cartProduct.product.id == product.id }

            if (cartProduct == null) {
                cartRepository.addCartProduct(product.id, ::onFailure) {
                    view.addCartSuccessView()
                    view.exitProductDetailView()

                    cartRepository.loadAllCarts(::onFailure) { reLoadCarts ->
                        val reCartProduct =
                            reLoadCarts.find { cartProduct -> cartProduct.product.id == product.id }
                                ?: return@loadAllCarts
                        this.carts.add(reCartProduct.toUIModel())

                        if (count > 1) {
                            val newCartProduct = reCartProduct.copy(quantity = count)
                            cartRepository.updateCartCount(newCartProduct, ::onFailure) {
                                view.addCartSuccessView()
                                view.exitProductDetailView()
                            }
                        }
                    }
                }
            }

            cartProduct?.let {
                val newCartProduct = cartProduct.copy(quantity = count)
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
    }
}
