package woowacourse.shopping.feature.product

import com.example.domain.Pagination
import com.example.domain.cart.Cart
import com.example.domain.cart.CartProduct
import com.example.domain.product.Product
import woowacourse.shopping.data.cart.CartRepository
import woowacourse.shopping.data.product.ProductRepository
import woowacourse.shopping.data.recentproduct.RecentProductRepository
import woowacourse.shopping.model.CartProductState
import woowacourse.shopping.model.CartProductState.Companion.MAX_COUNT_VALUE
import woowacourse.shopping.model.CartProductState.Companion.MIN_COUNT_VALUE
import woowacourse.shopping.model.ProductState
import woowacourse.shopping.model.RecentProductState
import woowacourse.shopping.model.mapper.toDomain
import woowacourse.shopping.model.mapper.toUi
import java.time.LocalDateTime

class MainPresenter(
    private val view: MainContract.View,
    private val productRepository: ProductRepository,
    private val recentProductRepository: RecentProductRepository,
    private val cartRepository: CartRepository
) : MainContract.Presenter {

    private val loadItemCountUnit = 20
    private var currentPage: Int = 1

    override fun loadMoreProducts() {
        productRepository.requestFetchProductsUnit(
            failure = {
                view.showEmptyProducts()
                view.setProducts(listOf())
            },
            success = { products, pagination ->
                view.addProductItems(products.map(Product::toUi))
                loadCartProductsQuantity()
                view.showProducts()
                currentPage = pagination.currentPage + 1
            }, unitSize = loadItemCountUnit, page = currentPage
        )
    }

    override fun loadRecentProducts() {
        view.setRecentProducts(recentProductRepository.getAll())
    }

    override fun loadCartSizeBadge() {
        view.showCartProductCountBadge()
        cartRepository.requestFetchCartProductsUnit(
            unitSize = 0, page = 0, failure = {},
            success = { cartProducts: List<CartProduct>, pagination: Pagination ->
                if (MIN_COUNT_VALUE <= pagination.total) view.setCartProductCountBadge(pagination.total)
            }
        )
    }

    override fun loadCartProductsQuantity() {
        cartRepository.requestFetchCartProductsUnit(
            unitSize = Cart.MAX_SIZE, page = 1, failure = {},
            success = { cartProducts: List<CartProduct>, pagination: Pagination ->
                view.setCartProductCounts(cartProducts)
                currentPage = pagination.currentPage + 1
            }
        )
    }

    override fun addRecentProduct(product: Product) {
        val nowDateTime: LocalDateTime = LocalDateTime.now()
        recentProductRepository.addRecentProduct(product, nowDateTime)

        storeRecentProduct(product, nowDateTime)
        view.setRecentProducts(recentProductRepository.getAll())
    }

    override fun showProductDetail(productState: ProductState) {
        val recentProduct: RecentProductState? =
            recentProductRepository.getMostRecentProduct()?.toUi()
        view.showProductDetail(productState, recentProduct)
        addRecentProduct(productState.toDomain())
    }

    override fun storeCartProduct(productState: ProductState) {
        cartRepository.addCartProduct(productState.id, { }, { })
        loadCartProductsQuantity()
        loadCartSizeBadge()
    }

    override fun minusCartProductQuantity(cartProductState: CartProductState) {
        cartProductState.quantity = (--cartProductState.quantity).coerceAtLeast(MIN_COUNT_VALUE)
        if (cartProductState.quantity - 1 == 0) {
            cartRepository.deleteCartProduct(
                id = cartProductState.id, success = {}, failure = {}
            )
        }
        cartRepository.updateCartProductQuantity(
            id = cartProductState.id, quantity = cartProductState.quantity,
            failure = {}, success = { loadCartSizeBadge() }
        )
    }

    override fun plusCartProductQuantity(cartProductState: CartProductState) {
        cartProductState.quantity = (++cartProductState.quantity).coerceAtMost(MAX_COUNT_VALUE)
        cartRepository.updateCartProductQuantity(
            id = cartProductState.id, quantity = cartProductState.quantity,
            failure = {}, success = {}
        )
    }

    private fun storeRecentProduct(product: Product, viewedDateTime: LocalDateTime) {
        recentProductRepository.addRecentProduct(product, viewedDateTime)
    }
}
