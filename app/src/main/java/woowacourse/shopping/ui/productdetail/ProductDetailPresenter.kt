package woowacourse.shopping.ui.productdetail

import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.domain.user.User
import woowacourse.shopping.repository.CartItemRepository
import woowacourse.shopping.repository.ProductRepository
import woowacourse.shopping.repository.RecentlyViewedProductRepository
import woowacourse.shopping.repository.UserRepository
import woowacourse.shopping.ui.productdetail.uistate.LastViewedProductUIState.Companion.toUIState
import woowacourse.shopping.ui.productdetail.uistate.ProductDetailUIState.Companion.toUIState
import java.time.LocalDateTime

class ProductDetailPresenter(
    private val view: ProductDetailContract.View,
    private val productRepository: ProductRepository,
    private val cartItemRepository: CartItemRepository,
    userRepository: UserRepository,
    private val recentlyViewedProductRepository: RecentlyViewedProductRepository
) : ProductDetailContract.Presenter {
    private val currentUser: User = userRepository.findCurrent().get().getOrElse { throw it }

    override fun loadProduct(productId: Long) {
        productRepository.findById(productId).thenAccept {
            val product = it.getOrThrow()
            val isProductExistInCart =
                cartItemRepository.existByProductId(product.id, currentUser).get().getOrThrow()
            view.setProduct(product.toUIState(isProductExistInCart))
            recentlyViewedProductRepository.save(product, LocalDateTime.now())
        }.exceptionally {
            view.showError(it.message.orEmpty())
            null
        }
    }

    override fun addProductToCart(productId: Long, count: Int) {
        require(count > 0) { ERROR_PRODUCT_COUNT_ZERO }

        productRepository.findById(productId).thenCompose {
            val product = it.getOrThrow()
            val cartItem = CartItem(-1, count, product)
            val savedCartItem = cartItemRepository.save(cartItem, currentUser).get().getOrThrow()
            cartItemRepository.updateCountById(savedCartItem.id, count, currentUser)
        }.thenAccept {
            it.getOrThrow()
            view.showCartView()
        }.exceptionally {
            view.showError(it.message.orEmpty())
            null
        }
    }

    override fun showCartCounter(productId: Long) {
        productRepository.findById(productId).thenAccept {
            val product = it.getOrThrow()
            view.openCartCounter(product.toUIState(false))
        }.exceptionally {
            view.showError(it.message.orEmpty())
            null
        }
    }

    override fun loadLastViewedProduct() {
        recentlyViewedProductRepository.findFirst10OrderByViewedTimeDesc().thenAccept {
            val recentlyViewedProducts = it.getOrThrow()
            if (recentlyViewedProducts.isEmpty()) {
                view.setLastViewedProduct(null)
                return@thenAccept
            }

            val productDetailUIState = recentlyViewedProducts.first().toUIState()
            view.setLastViewedProduct(productDetailUIState)
        }.exceptionally {
            view.showError(it.message.orEmpty())
            null
        }
    }

    companion object {
        private const val ERROR_PRODUCT_COUNT_ZERO = "장바구니에 상품을 추가하려면 개수는 1개 이상이어야 합니다."
    }
}
