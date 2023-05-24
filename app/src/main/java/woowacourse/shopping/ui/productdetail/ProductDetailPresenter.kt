package woowacourse.shopping.ui.productdetail

import woowacourse.shopping.domain.CartItem
import woowacourse.shopping.repository.CartItemRepository
import woowacourse.shopping.repository.ProductRepository
import woowacourse.shopping.repository.RecentlyViewedProductRepository
import woowacourse.shopping.ui.productdetail.uistate.LastViewedProductUIState
import woowacourse.shopping.ui.productdetail.uistate.ProductDetailUIState
import java.time.LocalDateTime

class ProductDetailPresenter(
    private val view: ProductDetailContract.View,
    private val productRepository: ProductRepository,
    private val cartItemRepository: CartItemRepository,
    private val recentlyViewedProductRepository: RecentlyViewedProductRepository
) : ProductDetailContract.Presenter {
    override fun onLoadProduct(productId: Long) {
        productRepository.findById(productId) { product ->
            product ?: return@findById
            cartItemRepository.existByProductId(product.id) { cartItem ->
                view.setProduct(
                    ProductDetailUIState.create(product, cartItem)
                )
                recentlyViewedProductRepository.save(product, LocalDateTime.now()) { }
            }
        }
    }

    override fun onAddProductToCart(productId: Long, count: Int) {
        require(count > 0) { "장바구니에 상품을 추가하려면 개수는 1개 이상이어야 합니다." }
        productRepository.findById(productId) { product ->
            product ?: return@findById
            println(product)
            println(count)
            val cartItem = CartItem(-1, product, LocalDateTime.now(), count)
            cartItemRepository.save(cartItem) { savedCartItem ->
                cartItemRepository.updateCountById(savedCartItem.id, count) {
                    view.showCartView()
                }
            }
        }
    }

    override fun onLoadLastViewedProduct() {
        recentlyViewedProductRepository.findFirst10OrderByViewedTimeDesc { recentlyViewedProducts ->

            if (recentlyViewedProducts.size < 2) {
                view.setLastViewedProduct(null)
                return@findFirst10OrderByViewedTimeDesc
            }

            val productDetailUIState = LastViewedProductUIState.from(recentlyViewedProducts[1])
            view.setLastViewedProduct(productDetailUIState)
        }
    }
}
