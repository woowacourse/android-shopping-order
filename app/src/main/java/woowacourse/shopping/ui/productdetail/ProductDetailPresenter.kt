package woowacourse.shopping.ui.productdetail

import woowacourse.shopping.domain.CartItem
import woowacourse.shopping.repository.CartItemRepository
import woowacourse.shopping.repository.ProductRepository
import woowacourse.shopping.repository.RecentlyViewedProductRepository
import woowacourse.shopping.ui.productdetail.uistate.LastViewedProductUIState.Companion.toUIState
import woowacourse.shopping.ui.productdetail.uistate.ProductDetailUIState.Companion.toUIState
import java.time.LocalDateTime

class ProductDetailPresenter(
    private val view: ProductDetailContract.View,
    private val productRepository: ProductRepository,
    private val cartItemRepository: CartItemRepository,
    private val recentlyViewedProductRepository: RecentlyViewedProductRepository
) : ProductDetailContract.Presenter {
    override fun loadProduct(productId: Long) {
        productRepository.findById(productId) { product ->
            requireNotNull(product) { ERROR_PRODUCT_NULL.format(productId) }

            cartItemRepository.existByProductId(product.id) { cartItem ->
                view.setProduct(product.toUIState(cartItem))
                recentlyViewedProductRepository.save(product, LocalDateTime.now()) { }
            }
        }
    }

    override fun addProductToCart(productId: Long, count: Int) {
        require(count > 0) { ERROR_PRODUCT_COUNT_ZERO }

        productRepository.findById(productId) { product ->
            requireNotNull(product) { ERROR_PRODUCT_NULL.format(productId) }

            val cartItem = CartItem(-1, product, LocalDateTime.now(), count)
            cartItemRepository.save(cartItem) { savedCartItem ->
                cartItemRepository.updateCountById(savedCartItem.id, count) {
                    view.showCartView()
                }
            }
        }
    }

    override fun showCartCounter(productId: Long) {
        productRepository.findById(productId) { product ->
            requireNotNull(product) { ERROR_PRODUCT_NULL.format(productId) }

            view.openCartCounter(product.toUIState(false))
        }
    }

    override fun loadLastViewedProduct() {
        recentlyViewedProductRepository.findFirst10OrderByViewedTimeDesc { recentlyViewedProducts ->
            if (recentlyViewedProducts.isEmpty()) {
                view.setLastViewedProduct(null)
                return@findFirst10OrderByViewedTimeDesc
            }

            val productDetailUIState = recentlyViewedProducts.first().toUIState()
            view.setLastViewedProduct(productDetailUIState)
        }
    }

    companion object {
        private const val ERROR_PRODUCT_COUNT_ZERO = "장바구니에 상품을 추가하려면 개수는 1개 이상이어야 합니다."
        private const val ERROR_PRODUCT_NULL = "상품을 서버에서 조회하지 못했습니다. 상품 ID : %d"
    }
}
