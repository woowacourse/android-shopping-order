package woowacourse.shopping.ui.shopping

import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.DomainCartProduct
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
import woowacourse.shopping.model.ProductModel
import woowacourse.shopping.model.UiProductCount
import woowacourse.shopping.model.UiRecentProduct
import woowacourse.shopping.model.mapper.toDomain
import woowacourse.shopping.model.mapper.toUi
import woowacourse.shopping.ui.shopping.ShoppingContract.Presenter
import woowacourse.shopping.ui.shopping.ShoppingContract.View

class ShoppingPresenter(
    view: View,
    private val productRepository: ProductRepository,
    private val recentProductRepository: RecentProductRepository,
    private val cartRepository: CartRepository,
    private val recentProductSize: Int = 10,
    private var recentProducts: RecentProducts = RecentProducts(),
    sizePerPage: Int = 20,
) : Presenter(view) {
    private var cart = Cart()
    private var currentPage: Page = LoadMore(sizePerPage = sizePerPage)
    private val cartProductCount: UiProductCount
        get() = UiProductCount(cart.productCountInCart)

    override fun fetchAll() {
        fetchAllCartProducts()
        fetchRecentProducts()
    }

    override fun fetchRecentProducts() {
        updateRecentProducts(recentProductRepository.getPartially(recentProductSize))
    }

    override fun loadMoreProducts() {
        currentPage = currentPage.next()
        updateCartView()
    }

    override fun addCartProduct(product: ProductModel, addCount: Int) {
        cartRepository.addCartProductByProductId(product.toDomain().id)
        fetchAllCartProducts()
    }

    override fun updateCartCount(cartProduct: CartProductModel, changedCount: Int) {
        cartRepository.updateProductCountById(cartProduct.toDomain().id, ProductCount(changedCount))
        fetchAllCartProducts()
    }

    override fun increaseCartCount(product: ProductModel, addCount: Int) {
        cartRepository.increaseProductCountByProductId(product.id, ProductCount(addCount))
        fetchAllCartProducts()
    }

    override fun navigateToCart() {
        view.navigateToCart()
    }

    override fun inquiryProductDetail(cartProduct: CartProductModel) {
        val recentProduct = RecentProduct(product = cartProduct.product.toDomain())
        view.navigateToProductDetail(cartProduct.product)
        updateRecentProducts(recentProducts + recentProduct)
    }

    override fun inquiryRecentProductDetail(recentProduct: UiRecentProduct) {
        view.navigateToProductDetail(recentProduct.product)
    }

    override fun inquiryOrders() {
        view.navigateToOrderList()
    }

    private fun fetchAllCartProducts() {
        productRepository.getAllProducts(
            onSuccess = { products -> transformCountedCartProduct(products, ::updateCart) },
            onFailed = { view.showErrorMessage(it.message ?: "") }
        )
    }

    private fun transformCountedCartProduct(
        products: List<Product>,
        onSuccess: (List<CartProduct>) -> Unit,
    ) {
        cartRepository.getAllCartProducts(
            onSuccess = { fetchedCartProducts ->
                val countedCartProduct = products.map { product ->
                    fetchedCartProducts.find { it.productId == product.id } ?: DomainCartProduct(
                        product = product,
                        selectedCount = ProductCount(0)
                    )
                }
                onSuccess(countedCartProduct)
            },
            onFailed = { view.showErrorMessage(it.message ?: "") },
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
