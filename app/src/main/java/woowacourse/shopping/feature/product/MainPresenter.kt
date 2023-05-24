package woowacourse.shopping.feature.product

import com.example.domain.Product
import com.example.domain.repository.CartRepository
import com.example.domain.repository.ProductRepository
import com.example.domain.repository.RecentProductRepository
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
    private var loadItemFromIndex = 0

    override fun loadMoreProducts() {
        productRepository.getAll(
            onFailure = {
                view.showEmptyProducts()
                view.setProducts(listOf())
            },
            onSuccess = {
                view.addProductItems(it.map(Product::toUi))
            }
        )
        loadItemFromIndex += loadItemCountUnit
    }

    override fun loadRecentProducts() {
        view.setRecentProducts(recentProductRepository.getAll())
    }

    override fun loadCartProductCountBadge() {
        view.showCartProductCountBadge()
        cartRepository.getAll(onFailure = {}, onSuccess = {
            if (it.size >= MIN_COUNT_VALUE) view.setCartProductCountBadge(it.size)
            else view.hideCartProductCount()
        })
    }

    override fun loadCartProductCounts() {
        cartRepository.getAll(onFailure = {}, onSuccess = {
            view.setCartProductCounts(it)
        })
    }

    override fun addRecentProduct(product: Product) {
        val nowDateTime: LocalDateTime = LocalDateTime.now()
        storeRecentProduct(product.id, nowDateTime)
        view.setRecentProducts(recentProductRepository.getAll())
    }

    override fun showProductDetail(productState: ProductState) {
        val recentProduct: RecentProductState? =
            recentProductRepository.getMostRecentProduct()?.toUi()
        view.showProductDetail(productState, recentProduct)
        addRecentProduct(productState.toDomain())
    }

    override fun storeCartProduct(productState: ProductState) {
//        cartRepository.addProduct(productState.id, MIN_COUNT_VALUE)
//        loadCartProductCountBadge()
    }

    override fun minusCartProductCount(productState: ProductState) {
//        val cartProduct: CartProduct? = cartRepository.getCartProduct(productState.id)
//        val cartProductCount: Int = (cartProduct?.quantity ?: MIN_COUNT_VALUE) - 1
//        cartRepository.updateCartProductCount(productState.id, cartProductCount)
//        loadCartProductCountBadge()
    }

    override fun plusCartProductCount(productState: ProductState) {
//        val cartProduct: CartProduct? = cartRepository.getCartProduct(productState.id)
//        val cartProductCount: Int = (cartProduct?.quantity ?: MIN_COUNT_VALUE) + 1
//        cartRepository.updateCartProductCount(productState.id, cartProductCount)
    }

    private fun storeRecentProduct(productId: Int, viewedDateTime: LocalDateTime) {
        recentProductRepository.addRecentProduct(productId, viewedDateTime)
    }
}
