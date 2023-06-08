package woowacourse.shopping.ui.shopping

import android.util.Log
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
import woowacourse.shopping.mapper.toDomain
import woowacourse.shopping.mapper.toUi
import woowacourse.shopping.model.UiCartProduct
import woowacourse.shopping.model.UiProduct
import woowacourse.shopping.model.UiProductCount
import woowacourse.shopping.model.UiRecentProduct
import woowacourse.shopping.ui.shopping.ShoppingContract.Presenter
import woowacourse.shopping.ui.shopping.ShoppingContract.View
import woowacourse.shopping.util.collection.DistinctList

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
    private val products: DistinctList<Product> = DistinctList()

    private val cartProductCount: UiProductCount
        get() = UiProductCount(cart.productCountInCart)

    override fun fetchAll() {
        loadProducts(currentPage)
        fetchRecentProducts()
    }

    override fun fetchRecentProducts() {
        updateRecentProducts(recentProductRepository.getPartially(recentProductSize))
    }

    override fun loadMoreProducts() {
        currentPage = currentPage.next()
        loadProducts(currentPage)
        updateCartView()
    }

    override fun inquiryProductDetail(cartProduct: UiCartProduct) {
        val recentProduct = RecentProduct(product = cartProduct.product.toDomain())
        view.navigateToProductDetail(cartProduct.product)
        updateRecentProducts(recentProducts + recentProduct)
    }

    override fun inquiryRecentProductDetail(recentProduct: UiRecentProduct) {
        view.navigateToProductDetail(recentProduct.product)
    }

    override fun inquiryCart() {
        view.navigateToCart()
    }

    override fun inquiryOrderHistory() {
        view.navigateToOrderHistory()
    }

    override fun addCartProduct(product: UiProduct, addCount: Int) {
        cartRepository.addCartProductByProductId(
            product.toDomain().id,
            onSuccess = {
                loadProducts(currentPage)
            },
            onFailure = {},
        )
    }

    override fun updateCartCount(cartProduct: UiCartProduct, changedCount: Int) {
        cartRepository.updateProductCountById(
            cartProduct.toDomain().id,
            ProductCount(changedCount),
            onSuccess = { loadProducts(currentPage) },
            onFailure = { },
        )
    }

    override fun increaseCartCount(product: UiProduct, addCount: Int) {
        cartRepository.increaseProductCountByProductId(
            product.id,
            ProductCount(addCount),
            onSuccess = { loadProducts(currentPage) },
            onFailure = { Log.d("error", "[ERROR] 데이터를 불러오는 데에 실패했습니다. : $it") },
        )
    }

    private fun loadProducts(page: Page) {
        productRepository.getAllProducts(
            page = page.getPageForCheckHasNext(),
            onSuccess = {
                products.addAll(it)
                Log.d("botto", "${products.size}")
                loadCartProducts { fetchedCartProducts ->
                    updateCart(transformCartProducts(products, fetchedCartProducts))
                }
                Log.d("test", "page value: ${page.value}")
            },
            onFailure = { Log.d("error", "[ERROR] 데이터를 불러오는 데에 실패했습니다. : $it") },
        )
    }

    private fun transformCartProducts(
        products: List<Product>,
        cartProducts: List<CartProduct>,
    ): List<CartProduct> = products.map { product ->
        cartProducts.find { it.productId == product.id } ?: DomainCartProduct(
            product = product,
            selectedCount = ProductCount(0),
        )
    }

    private fun loadCartProducts(onLoaded: (List<CartProduct>) -> Unit) {
        cartRepository.getAllCartProducts(
            onSuccess = { cartProducts -> onLoaded(cartProducts) },
            onFailure = { Log.d("error", "[ERROR] 데이터를 불러오는 데에 실패했습니다. : $it") },
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
        if (currentPage.hasNext(cart)) {
            showLoadMoreButton()
        } else {
            hideLoadMoreButton()
        }
    }

    private fun updateRecentProducts(newRecentProducts: RecentProducts) {
        recentProducts = recentProducts.update(newRecentProducts)
        view.updateRecentProducts(recentProducts.getItems().toUi())
    }
}
