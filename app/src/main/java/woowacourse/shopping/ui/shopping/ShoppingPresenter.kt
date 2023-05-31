package woowacourse.shopping.ui.shopping

import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.user.User
import woowacourse.shopping.repository.CartItemRepository
import woowacourse.shopping.repository.ProductRepository
import woowacourse.shopping.repository.RecentlyViewedProductRepository
import woowacourse.shopping.repository.UserRepository
import woowacourse.shopping.ui.shopping.uistate.ProductUIState
import woowacourse.shopping.ui.shopping.uistate.ProductUIState.Companion.toUIState
import woowacourse.shopping.ui.shopping.uistate.RecentlyViewedProductUIState.Companion.toUIState

class ShoppingPresenter(
    private val view: ShoppingContract.View,
    private val recentlyViewedProductRepository: RecentlyViewedProductRepository,
    private val productRepository: ProductRepository,
    private val cartItemRepository: CartItemRepository,
    private val userRepository: UserRepository
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
            userRepository.findCurrent { currentUser ->
                cartItemRepository.findAll(currentUser) { cartItems ->
                    val cartUIStates = createProductUIState(cartItems, products)
                    view.addProducts(cartUIStates)
                    refreshCanLoadMore()
                }
            }
        }
    }

    override fun refreshProducts() {
        productRepository.findAll(calculateOffset() + PAGE_SIZE, 0) { products ->
            userRepository.findCurrent { currentUser ->
                cartItemRepository.findAll(currentUser) { cartItems ->
                    val productUIStates = createProductUIState(cartItems, products)
                    view.setProducts(productUIStates)
                    refreshCanLoadMore()
                }
            }
        }
    }

    override fun addProductToCart(productId: Long) {
        productRepository.findById(productId) { product ->
            product ?: return@findById
            val cartItem = CartItem(-1, 1, product)
            userRepository.findCurrent { currentUser ->
                cartItemRepository.save(cartItem, currentUser) { savedCartItem ->
                    view.changeProduct(savedCartItem.toUIState())
                    loadCartItemCount()
                }
            }
        }
    }

    override fun plusCount(cartItemId: Long) {
        userRepository.findCurrent { currentUser ->
            cartItemRepository.findById(cartItemId, currentUser) { loadedCartItem ->
                requireNotNull(loadedCartItem) { ERROR_CART_ITEM_NULL.format(cartItemId) }
                val cartItem = loadedCartItem.plusQuantity()
                cartItemRepository.updateCountById(cartItemId, cartItem.quantity, currentUser) {
                    view.changeProduct(cartItem.toUIState())
                }
            }
        }
    }

    override fun minusCount(cartItemId: Long) {
        userRepository.findCurrent { currentUser ->
            cartItemRepository.findById(cartItemId, currentUser) { loadedCartItem ->
                requireNotNull(loadedCartItem) { ERROR_CART_ITEM_NULL.format(cartItemId) }

                if (loadedCartItem.quantity == 1) {
                    cartItemRepository.deleteById(cartItemId, currentUser) {
                        view.changeProduct(
                            loadedCartItem.copy(id = -1, quantity = 0).toUIState()
                        )
                        loadCartItemCount()
                    }
                    return@findById
                }
                val cartItem = loadedCartItem.minusQuantity()
                cartItemRepository.updateCountById(cartItemId, cartItem.quantity, currentUser) {
                    view.changeProduct(cartItem.toUIState())
                }
            }
        }
    }

    override fun loadCartItemCount() {
        userRepository.findCurrent { currentUser ->
            cartItemRepository.countAll(currentUser) { count ->
                view.setCartItemCount(count)
            }
        }
    }

    override fun openCart() {
        view.showCart()
    }

    override fun openOrderList() {
        view.showOrderList()
    }

    override fun loadUsers() {
        userRepository.findAll { users ->
            view.showUserList(users)
        }
    }

    override fun selectUser(user: User) {
        userRepository.saveCurrent(user)

        refreshProducts()
        loadCartItemCount()
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
