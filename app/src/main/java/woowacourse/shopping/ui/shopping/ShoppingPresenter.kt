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
    private lateinit var currentUser: User

    override fun loadRecentlyViewedProducts() {
        recentlyViewedProductRepository.findFirst10OrderByViewedTimeDesc()
            .onSuccess { recentlyViewedProducts ->
                val recentlyViewedProductUIStates = recentlyViewedProducts.map { it.toUIState() }
                view.setRecentlyViewedProducts(recentlyViewedProductUIStates)
            }.onFailure {
                return
            }
    }

    override fun loadProductsNextPage() {
        currentPage++
        val products = productRepository.findAll(PAGE_SIZE, calculateOffset()).getOrElse {
            return
        }
        val cartItems = cartItemRepository.findAll(currentUser).getOrElse {
            return
        }
        val cartUIStates = createProductUIState(cartItems, products)
        view.addProducts(cartUIStates)
        refreshCanLoadMore()
    }

    override fun refreshProducts() {
        val products = productRepository.findAll(calculateOffset() + PAGE_SIZE, 0).getOrElse {
            return
        }
        val cartItems = cartItemRepository.findAll(currentUser).getOrElse {
            return
        }
        val productUIStates = createProductUIState(cartItems, products)
        view.setProducts(productUIStates)
        refreshCanLoadMore()
    }

    override fun addProductToCart(productId: Long) {
        val product = productRepository.findById(productId).getOrElse {
            return
        }
        val cartItem = CartItem(-1, 1, product)
        cartItemRepository.save(cartItem, currentUser).onSuccess { savedCartItem ->
            view.changeProduct(savedCartItem.toUIState())
            loadCartItemCount()
        }.onFailure {
            return
        }
    }

    override fun plusCount(cartItemId: Long) {
        val loadedCartItem = cartItemRepository.findById(cartItemId, currentUser).getOrElse {
            return
        }

        val cartItem = loadedCartItem.plusQuantity()
        cartItemRepository.updateCountById(cartItemId, cartItem.quantity, currentUser).onSuccess {
            view.changeProduct(cartItem.toUIState())
        }.onFailure {
            return
        }
    }

    override fun minusCount(cartItemId: Long) {
        val loadedCartItem = cartItemRepository.findById(cartItemId, currentUser).getOrElse {
            return
        }

        if (loadedCartItem.quantity == 1) {
            cartItemRepository.deleteById(cartItemId, currentUser).onSuccess {
                view.changeProduct(loadedCartItem.copy(id = -1, quantity = 0).toUIState())
                loadCartItemCount()
            }
            return
        }
        val cartItem = loadedCartItem.minusQuantity()
        cartItemRepository.updateCountById(cartItemId, cartItem.quantity, currentUser).onSuccess {
            view.changeProduct(cartItem.toUIState())
        }.onFailure {
            return
        }
    }

    override fun loadCartItemCount() {
        cartItemRepository.countAll(currentUser).onSuccess { count ->
            view.setCartItemCount(count)
        }.onFailure {
            return
        }
    }

    override fun openCart() {
        view.showCart()
    }

    override fun openOrderList() {
        view.showOrderList()
    }

    override fun loadUsers() {
        userRepository.findAll().onSuccess { users ->
            view.showUserList(users)
        }.onFailure {
            return
        }
    }

    override fun selectUser(user: User) {
        userRepository.saveCurrent(user)
        currentUser = user

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
        productRepository.countAll().onSuccess { count ->
            val maxPage = (count - 1) / PAGE_SIZE + 1
            if (currentPage >= maxPage) view.setCanLoadMore(false)
        }.onFailure {
            return
        }
    }

    private fun calculateOffset() = (currentPage - 1) * PAGE_SIZE

    companion object {
        private const val PAGE_SIZE = 20
    }
}
