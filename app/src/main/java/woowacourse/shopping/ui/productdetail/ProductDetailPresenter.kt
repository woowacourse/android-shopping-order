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
    private val userRepository: UserRepository,
    private val recentlyViewedProductRepository: RecentlyViewedProductRepository
) : ProductDetailContract.Presenter {
    private lateinit var currentUser: User

    override fun loadCurrentUser() {
        currentUser = userRepository.findCurrent().getOrElse {
            return
        }
    }

    override fun loadProduct(productId: Long) {
        if (!::currentUser.isInitialized) return

        val product = productRepository.findById(productId).getOrElse {
            return
        }

        val cartItem = cartItemRepository.existByProductId(product.id, currentUser).getOrElse {
            return
        }
        view.setProduct(product.toUIState(cartItem))
        recentlyViewedProductRepository.save(product, LocalDateTime.now())
    }

    override fun addProductToCart(productId: Long, count: Int) {
        if (!::currentUser.isInitialized) return

        require(count > 0) { ERROR_PRODUCT_COUNT_ZERO }

        val product = productRepository.findById(productId).getOrElse {
            return
        }
        val cartItem = CartItem(-1, count, product)
        cartItemRepository.save(cartItem, currentUser).onSuccess { savedCartItem ->
            cartItemRepository.updateCountById(savedCartItem.id, count, currentUser)
            view.showCartView()
        }.onFailure {
            return
        }
    }

    override fun showCartCounter(productId: Long) {
        productRepository.findById(productId).onSuccess { product ->
            view.openCartCounter(product.toUIState(false))
        }.onFailure {
            return
        }
    }

    override fun loadLastViewedProduct() {
        val recentlyViewedProducts =
            recentlyViewedProductRepository.findFirst10OrderByViewedTimeDesc().getOrElse {
                return
            }
        if (recentlyViewedProducts.isEmpty()) {
            view.setLastViewedProduct(null)
            return
        }

        val productDetailUIState = recentlyViewedProducts.first().toUIState()
        view.setLastViewedProduct(productDetailUIState)
    }

    companion object {
        private const val ERROR_PRODUCT_COUNT_ZERO = "장바구니에 상품을 추가하려면 개수는 1개 이상이어야 합니다."
    }
}
