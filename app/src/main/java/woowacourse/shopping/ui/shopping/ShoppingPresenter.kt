package woowacourse.shopping.ui.shopping

import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.data.repository.RecentRepository
import woowacourse.shopping.mapper.toUIModel

class ShoppingPresenter(
    private val view: ShoppingContract.View,
    private val productRepository: ProductRepository,
    private val recentRepository: RecentRepository,
    private val cartRepository: CartRepository
) : ShoppingContract.Presenter {
    override fun setUpProducts() {
        setUpCartCounts()
        setUpNextProducts()
        setUpRecentProducts()
    }

    override fun setUpCartCounts() {
        cartRepository.getAll { carts ->
            val cartProducts = carts.all()
                .associateBy { it.product.id }
                .mapValues { it.value.quantity }
            view.setCartProducts(cartProducts)
        }
    }

    override fun setUpNextProducts() {
        productRepository.getAll { products ->
            if (products.isNullOrEmpty()) return@getAll
            view.addMoreProducts(products.map { it.toUIModel() })
        }
    }

    override fun setUpRecentProducts() {
        val recentProducts = recentRepository.getRecent(RECENT_PRODUCT_COUNT)
            .map { it.toUIModel() }
        view.setRecentProducts(recentProducts)
    }

    override fun setUpTotalCount() {
        view.setToolbar(
            cartRepository.getTotalSelectedCount()
        )
    }

    override fun updateItemCount(productId: Int, count: Int) {
        cartRepository.updateCount(productId, count) {
            cartRepository.getAll {
                setUpTotalCount()
            }
        }
    }

    override fun navigateToItemDetail(productId: Int) {
        productRepository.findById(productId) {
            if (it == null) return@findById
            view.navigateToProductDetail(it.toUIModel())
        }
    }

    companion object {
        private const val RECENT_PRODUCT_COUNT = 10
    }
}
