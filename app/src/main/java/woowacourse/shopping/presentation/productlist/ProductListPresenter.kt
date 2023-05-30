package woowacourse.shopping.presentation.productlist

import woowacourse.shopping.CartProductInfo
import woowacourse.shopping.CartProductInfoList
import woowacourse.shopping.Product
import woowacourse.shopping.presentation.mapper.toPresentation
import woowacourse.shopping.presentation.model.CartProductInfoModel
import woowacourse.shopping.repository.CartRepository
import woowacourse.shopping.repository.ProductRepository
import woowacourse.shopping.repository.RecentProductRepository

class ProductListPresenter(
    private val view: ProductListContract.View,
    private val productRepository: ProductRepository,
    private val recentProductRepository: RecentProductRepository,
    private val cartRepository: CartRepository,
) : ProductListContract.Presenter {

    private var size = PRODUCTS_SIZE
    override fun refreshProductItems() {
        view.setLoadingViewVisible(true)
        productRepository.getProductsWithRange(0, size) { products ->
            cartRepository.getAllCartItems { allCartItems ->
                val foundCartItems = products.findCartItem(allCartItems)
                view.loadProductItems(foundCartItems.map { it.toPresentation() })
                view.setLoadingViewVisible(false)
            }
        }
    }

    private fun List<Product>.findCartItem(cartItems: List<CartProductInfo>): List<CartProductInfo> {
        return this.map { product ->
            cartItems.find { cartItem -> product.id == cartItem.product.id }
                ?: makeQuantityZeroCartItem(product)
        }
    }

    private fun makeQuantityZeroCartItem(product: Product): CartProductInfo {
        return CartProductInfo(
            1000 + product.id,
            product,
            0,
        )
    }

    override fun loadMoreProductItems() {
        size += PRODUCTS_SIZE
        refreshProductItems()
    }

    override fun loadRecentProductItems() {
        recentProductRepository.getRecentProducts(RECENT_PRODUCTS_SIZE) { products ->
            view.loadRecentProductItems(products.map { it.toPresentation() })
        }
    }

    override fun updateCartCount() {
        cartRepository.getAllCartItems {
            view.showCartCount(CartProductInfoList(it).count)
        }
    }

    override fun addCartItem(cartProductModel: CartProductInfoModel) {
        cartRepository.addCartItem(cartProductModel.productModel.id) {
            updateCartCount()
            refreshProductItems()
        }
    }

    override fun updateCartItemQuantity(cartProductModel: CartProductInfoModel, count: Int) {
        if (count == 0) {
            cartRepository.deleteCartItem(cartProductModel.id) {
                updateCartCount()
                refreshProductItems()
            }
        } else {
            cartRepository.updateCartItemQuantity(
                cartProductModel.id,
                count,
            ) {
                updateCartCount()
                refreshProductItems()
            }
        }
    }

    override fun showMyCart() {
        cartRepository.getAllCartItems { cartProducts ->
            view.navigateToCart(cartProducts.map { it.toPresentation() })
        }
    }

    companion object {
        private const val PRODUCTS_SIZE = 20
        private const val RECENT_PRODUCTS_SIZE = 10
    }
}
