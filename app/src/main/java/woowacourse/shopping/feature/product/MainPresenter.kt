package woowacourse.shopping.feature.product

import android.util.Log
import com.example.domain.CartProduct
import com.example.domain.Pagination
import com.example.domain.Product
import com.example.domain.repository.CartRepository
import com.example.domain.repository.ProductRepository
import com.example.domain.repository.RecentProductRepository
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
    private var page: Int = 1

    override fun loadMoreProducts() {
        productRepository.requestFetchProductsUnit(
            onFailure = {
                view.showEmptyProducts()
                view.setProducts(listOf())
            },
            onSuccess = {
                loadCartProductCounts()
                view.addProductItems(it.map(Product::toUi))
                view.showProducts()
            }, unitSize = loadItemCountUnit, page = page++
        )
    }

    override fun loadRecentProducts() {
        view.setRecentProducts(recentProductRepository.getAll())
    }

    override fun loadCartProductCountBadge() {
        view.showCartProductCountBadge()
        cartRepository.requestFetchCartProductsUnit(
            unitSize = 0, page = 0, onFailure = {},
            onSuccess = { cartProducts: List<CartProduct>, pagination: Pagination ->
                if (MIN_COUNT_VALUE <= pagination.total) view.setCartProductCountBadge(pagination.total)
            }
        )
    }

    override fun loadCartProductCounts() {
        cartRepository.requestFetchCartProductsUnit(
            // todo UNIT SIZE, page는 ProductRepo로부터 pagination 받아와 적용하기
            unitSize = 100, page = 1, onFailure = {},
            onSuccess = { cartProducts: List<CartProduct>, pagination: Pagination ->
                Log.d("otter66", "page: $page")
                Log.d("otter66", "cart: $cartProducts")
                Log.d("otter66", "pagination: $pagination")
                view.setCartProductCounts(cartProducts)
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
        loadCartProductCounts()
        loadCartProductCountBadge()
    }

    override fun minusCartProductCount(cartProductState: CartProductState) {
        cartProductState.quantity = (--cartProductState.quantity).coerceAtLeast(MIN_COUNT_VALUE)
        if (cartProductState.quantity - 1 == 0) {
            cartRepository.deleteCartProduct(
                id = cartProductState.id,
                onSuccess = {},
                onFailure = {}
            )
        }
        cartRepository.updateCartProductQuantity(
            id = cartProductState.id, quantity = cartProductState.quantity,
            onFailure = {}, onSuccess = { loadCartProductCountBadge() }
        )
    }

    override fun plusCartProductCount(cartProductState: CartProductState) {
        cartProductState.quantity = (++cartProductState.quantity).coerceAtMost(MAX_COUNT_VALUE)
        cartRepository.updateCartProductQuantity(
            id = cartProductState.id, quantity = cartProductState.quantity,
            onFailure = {}, onSuccess = {}
        )
    }

    private fun storeRecentProduct(product: Product, viewedDateTime: LocalDateTime) {
        recentProductRepository.addRecentProduct(product, viewedDateTime)
    }
}
