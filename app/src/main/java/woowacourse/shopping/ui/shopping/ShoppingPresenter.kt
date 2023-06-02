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
import woowacourse.shopping.utils.ErrorHandler.handle

class ShoppingPresenter(
    private val view: ShoppingContract.View,
    private val recentlyViewedProductRepository: RecentlyViewedProductRepository,
    private val productRepository: ProductRepository,
    private val cartItemRepository: CartItemRepository,
    private val userRepository: UserRepository,
    private val pageSize: Int
) : ShoppingContract.Presenter {
    private var currentPage = 1
    private var currentUser: User

    init {
        val users = userRepository.findAll().get().getOrThrow()
        currentUser = users[0]
    }

    override fun loadRecentlyViewedProducts() {
        recentlyViewedProductRepository.findFirst10OrderByViewedTimeDesc()
            .thenAccept { recentlyViewedProduct ->
                val recentlyViewedProductUIStates =
                    recentlyViewedProduct.getOrThrow().map { it.toUIState() }
                view.setRecentlyViewedProducts(recentlyViewedProductUIStates)
            }.exceptionally {
                it.handle(view)
                null
            }
    }

    override fun loadProductsNextPage() {
        currentPage++
        productRepository.findAll(pageSize, calculateOffset()).thenApply { productResult ->
            val products = productResult.getOrThrow()
            val cartItems = cartItemRepository.findAll(currentUser).get().getOrThrow()
            createProductUIState(cartItems, products)
        }.thenAccept { cart ->
            view.addProducts(cart)
            refreshCanLoadMore()
        }.exceptionally {
            it.handle(view)
            null
        }
    }

    override fun refreshProducts() {
        productRepository.findAll(calculateOffset() + pageSize, 0).thenAccept { productsResult ->
            val products = productsResult.getOrThrow()
            val cartItems = cartItemRepository.findAll(currentUser).get().getOrThrow()
            val product = createProductUIState(cartItems, products)
            view.setProducts(product)
            refreshCanLoadMore()
        }.exceptionally {
            it.handle(view)
            null
        }
    }

    override fun addProductToCart(productId: Long) {
        productRepository.findById(productId).thenCompose { productResult ->
            val product = productResult.getOrThrow()
            val cartItem = CartItem(-1, 1, product)
            cartItemRepository.save(cartItem, currentUser)
        }.thenAccept { savedCartItemResult ->
            val savedCartItem = savedCartItemResult.getOrThrow()
            view.changeProduct(savedCartItem.toUIState())
            loadCartItemCount()
        }.exceptionally {
            it.handle(view)
            null
        }
    }

    override fun plusCartItemQuantity(cartItemId: Long) {
        cartItemRepository.findById(cartItemId, currentUser).thenApply { loadedCartItemResult ->
            val loadedCartItem = loadedCartItemResult.getOrThrow()
            loadedCartItem.plusQuantity()
        }.thenApply { cartItem ->
            cartItemRepository.updateCountById(cartItemId, cartItem.quantity, currentUser).get()
            view.changeProduct(cartItem.toUIState())
        }.exceptionally {
            it.handle(view)
        }
    }

    override fun minusCartItemQuantity(cartItemId: Long) {
        cartItemRepository.findById(cartItemId, currentUser).thenApply { loadedCartItemResult ->
            val loadedCartItem = loadedCartItemResult.getOrThrow()
            minusCartItem(loadedCartItem, cartItemId)
        }.exceptionally {
            it.handle(view)
        }
    }

    override fun loadCartItemCount() {
        cartItemRepository.countAll(currentUser).thenAccept { countResult ->
            val count = countResult.getOrThrow()
            view.setCartItemCount(count)
        }.exceptionally {
            it.handle(view)
            null
        }
    }

    override fun openCart() {
        view.showCart()
    }

    override fun openOrderList() {
        view.showOrderList()
    }

    override fun loadUsers() {
        userRepository.findAll().thenAccept { usersResult ->
            val users = usersResult.getOrThrow()
            view.showUserList(users)
        }.exceptionally {
            it.handle(view)
            null
        }
    }

    override fun selectUser(user: User) {
        userRepository.saveCurrent(user)
        currentUser = user

        refreshProducts()
        loadCartItemCount()
    }

    private fun minusCartItem(
        loadedCartItem: CartItem,
        cartItemId: Long
    ) {
        if (loadedCartItem.quantity == 1) {
            deleteCartItem(loadedCartItem, cartItemId)
            return
        }
        val cartItem = loadedCartItem.minusQuantity()
        cartItemRepository.updateCountById(cartItemId, cartItem.quantity, currentUser).thenAccept {
            view.changeProduct(cartItem.toUIState())
        }
    }

    private fun deleteCartItem(loadedCartItem: CartItem, cartItemId: Long) {
        cartItemRepository.deleteById(cartItemId, currentUser).get().getOrThrow()
        view.changeProduct(loadedCartItem.copy(id = -1, quantity = 0).toUIState())
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
        val count = productRepository.countAll().get().getOrThrow()
        val maxPage = (count - 1) / pageSize + 1
        if (currentPage >= maxPage) view.setCanLoadMore(false)
    }

    private fun calculateOffset() = (currentPage - 1) * pageSize
}
