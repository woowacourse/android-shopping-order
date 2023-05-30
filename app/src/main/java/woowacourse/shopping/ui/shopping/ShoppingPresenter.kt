package woowacourse.shopping.ui.shopping

import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.repository.CartItemRepository
import woowacourse.shopping.repository.ProductRepository
import woowacourse.shopping.repository.RecentlyViewedProductRepository
import woowacourse.shopping.ui.shopping.uistate.ProductUIState
import woowacourse.shopping.ui.shopping.uistate.ProductUIState.Companion.toUIState
import woowacourse.shopping.ui.shopping.uistate.RecentlyViewedProductUIState.Companion.toUIState
import java.time.LocalDateTime

class ShoppingPresenter(
    private val view: ShoppingContract.View,
    private val recentlyViewedProductRepository: RecentlyViewedProductRepository,
    private val productRepository: ProductRepository,
    private val cartItemRepository: CartItemRepository
) : ShoppingContract.Presenter {

    private var currentPage = 1

    override fun loadRecentlyViewedProducts() {
        recentlyViewedProductRepository.findFirst10OrderByViewedTimeDesc { recentlyViewedProducts ->
            val recentlyViewedProductUIStates = recentlyViewedProducts.map { it.toUIState() }
            view.setRecentlyViewedProducts(recentlyViewedProductUIStates)
        }
    }

    override fun loadProductsNextPage() {
        currentPage++
        productRepository.findAll(PAGE_SIZE, calculateOffset()) { products ->
            cartItemRepository.findAll { cartItems ->
                val cartUIStates = createProductUIState(cartItems, products)
                view.addProducts(cartUIStates)
                refreshCanLoadMore()
            }
        }
    }

    override fun refreshProducts() {
        productRepository.findAll(calculateOffset() + PAGE_SIZE, 0) { products ->
            cartItemRepository.findAll { cartItems ->
                val productUIStates = createProductUIState(cartItems, products)
                view.setProducts(productUIStates)
                refreshCanLoadMore()
            }
        }
    }

    override fun addProductToCart(productId: Long) {
        productRepository.findById(productId) { product ->
            product ?: return@findById
            val cartItem = CartItem(-1, 1, product, LocalDateTime.now())
            cartItemRepository.save(cartItem) { savedCartItem ->
                view.changeProduct(savedCartItem.toUIState())
                loadCartItemCount()
            }
        }
    }

    override fun plusCount(cartItemId: Long) {
        cartItemRepository.findById(cartItemId) { loadedCartItem ->
            requireNotNull(loadedCartItem) { ERROR_CART_ITEM_NULL.format(cartItemId) }
            val cartItem = loadedCartItem.plusQuantity()
            cartItemRepository.updateCountById(cartItemId, cartItem.quantity) {
                view.changeProduct(cartItem.toUIState())
            }
        }
    }

    override fun minusCount(cartItemId: Long) {
        cartItemRepository.findById(cartItemId) { loadedCartItem ->
            requireNotNull(loadedCartItem) { ERROR_CART_ITEM_NULL.format(cartItemId) }

            if (loadedCartItem.quantity == 1) {
                cartItemRepository.deleteById(cartItemId) {
                    view.changeProduct(
                        loadedCartItem.copy(id = -1, quantity = 0).toUIState()
                    )
                    loadCartItemCount()
                }
                return@findById
            }
            val cartItem = loadedCartItem.minusQuantity()
            cartItemRepository.updateCountById(cartItemId, cartItem.quantity) {
                view.changeProduct(cartItem.toUIState())
            }
        }
    }

    override fun loadCartItemCount() {
        cartItemRepository.countAll { count ->
            view.setCartItemCount(count)
        }
    }

    override fun openCart() {
        view.showCart()
    }

    override fun openOrderList() {
        view.showOrderList()
    }

    private fun createProductUIState(
        cartItems: List<CartItem>,
        products: List<Product>
    ): List<ProductUIState> {
        val cartItemMap = cartItems.associateBy { it.product.id }
        return products.map { product ->
            if (cartItemMap[product.id] != null) {
                cartItemMap[product.id]!!.toUIState()
            } else {
                product.toUIState()
            }
        }
    }

    private fun refreshCanLoadMore() {
        productRepository.countAll { count ->
            val maxPage = (count - 1) / PAGE_SIZE + 1
            if (currentPage >= maxPage) view.setCanLoadMore(false)
        }
    }

    private fun calculateOffset() = (currentPage - 1) * PAGE_SIZE

    companion object {
        private const val PAGE_SIZE = 20
        private const val ERROR_CART_ITEM_NULL = "장바구니 상품을 서버에서 조회하지 못했습니다. 상품 ID : %d"
    }
}
