package woowacourse.shopping.presentation.view.productdetail

import woowacourse.shopping.data.mapper.toUIModel
import woowacourse.shopping.data.respository.cart.CartRepository
import woowacourse.shopping.data.respository.product.ProductRepository
import woowacourse.shopping.presentation.model.CartModel
import woowacourse.shopping.presentation.model.RecentProductModel

class ProductDetailPresenter(
    private val view: ProductDetailContract.View,
    productId: Long,
    productRepository: ProductRepository,
    private val cartRepository: CartRepository,
) : ProductDetailContract.Presenter {
    private lateinit var product: CartModel

    init {
        productRepository.loadDataById(productId, ::onFailure) { cartRemoteEntity ->
            product = cartRemoteEntity.toUIModel()
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
            val cartRemoteEntity = carts.find { cartRemoteEntity -> cartRemoteEntity.productEntity.id == product.product.id }

            if (cartRemoteEntity == null) {
                cartRepository.addCartProduct(product.product.id, ::onFailure) {
                    if (count == UPDATE_COUNT_CONDITION) {
                        cartRepository.loadAllCarts(::onFailure) { reLoadCarts ->
                            val reCartProduct =
                                reLoadCarts.find { cartProduct -> cartProduct.productEntity.id == product.id }
                                    ?: return@loadAllCarts

                            cartRepository.addLocalCart(reCartProduct.id)
                        }

                        view.addCartSuccessView()
                        view.exitProductDetailView()
                        return@addCartProduct
                    }

                    cartRepository.loadAllCarts(::onFailure) { reLoadCarts ->
                        val reCartProduct =
                            reLoadCarts.find { cartProduct -> cartProduct.productEntity.id == product.id }
                                ?: return@loadAllCarts

                        val newCartProduct = reCartProduct.copy(quantity = count)
                        cartRepository.updateCartCount(newCartProduct, ::onFailure) {
                            view.addCartSuccessView()
                            view.exitProductDetailView()
                        }
                    }
                }
            }

            cartRemoteEntity?.let { cart ->
                val newCartRemoteEntity = cartRemoteEntity.copy(quantity = cart.quantity + count)
                cartRepository.updateCartCount(newCartRemoteEntity, ::onFailure) {
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
