package woowacourse.shopping.ui.productdetail

import android.os.Handler
import android.os.Looper
import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.repository.CartItemRepository
import woowacourse.shopping.repository.ProductRepository
import woowacourse.shopping.repository.RecentlyViewedProductRepository
import woowacourse.shopping.ui.productdetail.uistate.LastViewedProductUIState.Companion.toUIState
import woowacourse.shopping.ui.productdetail.uistate.ProductDetailUIState.Companion.toUIState
import woowacourse.shopping.utils.ErrorHandler.handle
import java.time.LocalDateTime

class ProductDetailPresenter(
    private val view: ProductDetailContract.View,
    private val productRepository: ProductRepository,
    private val cartItemRepository: CartItemRepository,
    private val recentlyViewedProductRepository: RecentlyViewedProductRepository,
    private val mainLooperHandler: Handler = Handler(Looper.getMainLooper())
) : ProductDetailContract.Presenter {

    override fun loadProduct(productId: Long) {
        productRepository.findById(productId).thenAccept {
            val product = it.getOrThrow()
            val isProductExistInCart =
                cartItemRepository.existByProductId(product.id).get().getOrThrow()
            mainLooperHandler.post {
                view.setProduct(product.toUIState(isProductExistInCart))
            }
            recentlyViewedProductRepository.save(product, LocalDateTime.now())
        }.exceptionally {
            it.handle(view)
            null
        }
    }

    override fun addProductToCart(productId: Long, count: Int) {
        require(count > 0) { ERROR_PRODUCT_COUNT_ZERO }

        productRepository.findById(productId).thenCompose {
            val product = it.getOrThrow()
            val cartItem = CartItem(-1, count, product)
            val savedCartItem = cartItemRepository.save(cartItem).get().getOrThrow()
            cartItemRepository.updateCountById(savedCartItem.id, count)
        }.thenAccept {
            it.getOrThrow()
            mainLooperHandler.post {
                view.showCartView()
            }
        }.exceptionally {
            it.handle(view)
            null
        }
    }

    override fun showCartCounter(productId: Long) {
        productRepository.findById(productId).thenAccept {
            val product = it.getOrThrow()
            mainLooperHandler.post {
                view.openCartCounter(product.toUIState(false))
            }
        }.exceptionally {
            it.handle(view)
            null
        }
    }

    override fun loadLastViewedProduct(limit: Int) {
        recentlyViewedProductRepository.findLimitedOrderByViewedTimeDesc(limit).thenAccept {
            val recentlyViewedProducts = it.getOrThrow()
            if (recentlyViewedProducts.isEmpty()) {
                mainLooperHandler.post {
                    view.setLastViewedProduct(null)
                }
                return@thenAccept
            }

            val productDetailUIState = recentlyViewedProducts.first().toUIState()
            mainLooperHandler.post {
                view.setLastViewedProduct(productDetailUIState)
            }
        }.exceptionally {
            it.handle(view)
            null
        }
    }

    companion object {
        private const val ERROR_PRODUCT_COUNT_ZERO = "장바구니에 상품을 추가하려면 개수는 1개 이상이어야 합니다."
    }
}
