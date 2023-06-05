package woowacourse.shopping.ui.shopping

import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductCount
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.model.RecentProducts
import woowacourse.shopping.domain.model.page.LoadMore
import woowacourse.shopping.domain.model.page.Page
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.model.CartProductModel
import woowacourse.shopping.model.ProductCountModel
import woowacourse.shopping.model.ProductModel
import woowacourse.shopping.model.RecentProductModel
import woowacourse.shopping.model.mapper.toDomain
import woowacourse.shopping.model.mapper.toUi
import woowacourse.shopping.ui.shopping.ShoppingContract.Presenter
import woowacourse.shopping.ui.shopping.ShoppingContract.View
import woowacourse.shopping.util.collection.DistinctList

class ShoppingPresenter(
    private val view: View,
    private val productRepository: ProductRepository,
    private val recentProductRepository: RecentProductRepository,
    private val cartRepository: CartRepository,
    private val recentProductSize: Int = 10,
    private var recentProducts: RecentProducts = RecentProducts(),
    sizePerPage: Int = 20,
) : Presenter {
    private var cart = Cart()
    private val products: DistinctList<Product> = DistinctList()
    private var currentPage: Page = LoadMore(sizePerPage = sizePerPage)
    private val cartProductCount: ProductCountModel
        get() = ProductCountModel(cart.productCountInCart)

    override fun fetchAll() {
        loadProducts(currentPage)
        fetchRecentProducts()
    }

    override fun fetchRecentProducts() {
        updateRecentProducts(recentProductRepository.getRecentProducts(recentProductSize))
    }

    override fun loadMoreProducts() {
        currentPage = currentPage.next()
        loadProducts(currentPage)
        updateCartView()
    }

    override fun inquiryProductDetail(cartProduct: CartProductModel) {
        val recentProduct = RecentProduct(product = cartProduct.product.toDomain())
        view.navigateToProductDetail(cartProduct.product)
        updateRecentProducts(recentProducts + recentProduct)
    }

    override fun inquiryRecentProductDetail(recentProduct: RecentProductModel) {
        view.navigateToProductDetail(recentProduct.product)
    }

    override fun inquiryCart() {
        view.navigateToCart()
    }

    override fun inquiryOrders() {
        view.navigateToOrderList()
    }

    override fun addCartProduct(product: ProductModel, addCount: Int) {
        cartRepository.saveCartProductByProductId(
            product.toDomain().id,
            onSuccess = { loadProducts(currentPage) },
            onFailed = { view.showCartProductSaveFailed() },
        )
    }

    override fun updateCartCount(cartProduct: CartProductModel, changedCount: Int) {
        cartRepository.updateProductCountById(
            cartProduct.toDomain().id,
            ProductCount(changedCount),
            onSuccess = { loadProducts(currentPage) },
            onFailed = { view.showCartCountChangedFailed() },
        )
    }

    override fun increaseCartCount(product: ProductModel, addCount: Int) {
        cartRepository.increaseProductCountByProductId(
            product.id,
            ProductCount(addCount),
            onSuccess = { loadProducts(currentPage) },
            onFailed = { view.showCartCountChangedFailed() },
        )
    }

    private fun loadProducts(page: Page) {
        productRepository.getProductsByPage(
            page = page.getPageForCheckHasNext(),
            onSuccess = { fetchedProducts ->
                products.addAll(fetchedProducts)
                loadCartProducts { fetchedCartProducts ->
                    updateCart(transformCartProducts(products, fetchedCartProducts))
                }
            },
            onFailed = { view.showProductLoadFailed() },
        )
    }

    private fun transformCartProducts(
        products: List<Product>,
        cartProducts: List<CartProduct>,
    ): List<CartProduct> = products.map { product ->
        cartProducts.find { it.productId == product.id } ?: CartProduct(
            product = product,
            selectedCount = ProductCount(0),
        )
    }

    private fun loadCartProducts(onLoaded: (List<CartProduct>) -> Unit) {
        cartRepository.getAllCartProducts(
            onSuccess = { cartProducts -> onLoaded(cartProducts) },
            onFailed = {},
        )
    }

    private fun updateCart(newCartProducts: List<CartProduct>) {
        cart = cart.update(newCartProducts)
        updateCartView()
    }

    private fun updateCartView() {
        view.updateProducts(currentPage.takeItems(cart).toUi())
        view.updateCartBadge(cartProductCount)
        view.updateLoadMoreVisible()
    }

    private fun View.updateLoadMoreVisible() {
        if (currentPage.hasNext(cart)) showLoadMoreButton() else hideLoadMoreButton()
    }

    private fun updateRecentProducts(newRecentProducts: RecentProducts) {
        recentProducts = recentProducts.update(newRecentProducts)
        view.updateRecentProducts(recentProducts.getItems().toUi())
    }
}
