package woowacourse.shopping.ui.products

import woowacourse.shopping.domain.CartItem
import woowacourse.shopping.domain.Product
import woowacourse.shopping.repository.CartItemRepository
import woowacourse.shopping.repository.ProductRepository
import woowacourse.shopping.repository.RecentlyViewedProductRepository
import woowacourse.shopping.ui.products.uistate.ProductUIState
import woowacourse.shopping.ui.products.uistate.RecentlyViewedProductUIState
import java.time.LocalDateTime

class ProductListPresenter(
    private val view: ProductListContract.View,
    private val recentlyViewedProductRepository: RecentlyViewedProductRepository,
    private val productRepository: ProductRepository,
    private val cartItemRepository: CartItemRepository
) : ProductListContract.Presenter {

    private var currentPage = 1
    private val offset
        get() = (currentPage - 1) * PAGE_SIZE

    override fun getCurrentPage(): Int {
        return currentPage
    }

    override fun onLoadRecentlyViewedProducts() {
        recentlyViewedProductRepository.findFirst10OrderByViewedTimeDesc { recentlyViewedProducts ->
            val recentlyViewedProductUIStates =
                recentlyViewedProducts.map(RecentlyViewedProductUIState::from)
            view.setRecentlyViewedProducts(recentlyViewedProductUIStates)
        }
    }

    override fun onLoadProductsNextPage() {
        currentPage++
        productRepository.findAll(PAGE_SIZE, offset) { products ->
            cartItemRepository.findAll { cartItems ->
                val productUIStates = createProductUIStates(cartItems, products)
                view.addProducts(productUIStates)
                refreshCanLoadMore()
            }
        }
    }

    private fun createProductUIStates(
        cartItems: List<CartItem>,
        products: List<Product>
    ): List<ProductUIState> {
        val cartItemMap = cartItems.associateBy { it.product.id }
        return products.map { product ->
            val cartItem = cartItemMap[product.id]
            if (cartItem != null) {
                ProductUIState.from(cartItem)
            } else {
                ProductUIState.from(product)
            }
        }
    }

    override fun onRefreshProducts() {
        productRepository.findAll(offset + PAGE_SIZE, 0) { products ->
            cartItemRepository.findAll { cartItems ->
                val productUIStates = createProductUIStates(cartItems, products)
                view.setProducts(productUIStates)
                refreshCanLoadMore()
            }
        }
    }

    private fun refreshCanLoadMore() {
        productRepository.countAll { count ->
            val maxPage = (count - 1) / PAGE_SIZE + 1
            if (currentPage >= maxPage) view.setCanLoadMore(false)
        }
    }

    override fun onAddToCart(productId: Long) {
        println(productId)
        productRepository.findById(productId) { product ->
            product ?: return@findById
            val cartItem = CartItem(-1, product, LocalDateTime.now(), 1)
            cartItemRepository.save(cartItem) { savedCartItem ->
                view.replaceProduct(ProductUIState.from(savedCartItem))
                onLoadCartItemCount()
            }
        }
    }

    override fun onPlusCount(cartItemId: Long) {
        cartItemRepository.findById(cartItemId) { cartItem ->
            cartItem.plusCount()
            cartItemRepository.updateCountById(cartItemId, cartItem.count) {
                view.replaceProduct(ProductUIState.from(cartItem))
            }
        }
    }

    override fun onMinusCount(cartItemId: Long) {
        cartItemRepository.findById(cartItemId) { cartItem ->
            if (cartItem.count == 1) {
                cartItemRepository.deleteById(cartItemId) {
                    view.replaceProduct(ProductUIState.from(cartItem.product))
                    onLoadCartItemCount()
                }
                return@findById
            }
            cartItem.minusCount()
            cartItemRepository.updateCountById(cartItemId, cartItem.count) {
                view.replaceProduct(ProductUIState.from(cartItem))
            }
        }
    }

    override fun onLoadCartItemCount() {
        cartItemRepository.countAll { count ->
            view.setCartItemCount(count)
        }
    }

    companion object {
        private const val PAGE_SIZE = 20
    }
}
