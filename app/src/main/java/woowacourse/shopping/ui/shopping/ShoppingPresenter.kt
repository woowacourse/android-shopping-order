package woowacourse.shopping.ui.shopping

import woowacourse.shopping.mapper.toUIModel
import woowacourse.shopping.repository.CartRepository
import woowacourse.shopping.repository.ProductRepository
import woowacourse.shopping.repository.RecentRepository

class ShoppingPresenter(
    private val view: ShoppingContract.View,
    private val productRepository: ProductRepository,
    private val recentRepository: RecentRepository,
    private val cartRepository: CartRepository,
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
            products?.let {
                view.addMoreProducts(products.map { it.toUIModel() })
            }
        }
    }

    override fun setUpRecentProducts() {
        val recentProducts = recentRepository.getRecent(RECENT_PRODUCT_COUNT)
            .map { it.toUIModel() }
        view.setRecentProducts(recentProducts)
    }

    override fun setUpTotalCount() {
        view.setToolbar(
            cartRepository.getTotalSelectedCount(),
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
            it?.let {
                view.navigateToProductDetail(it.toUIModel())
            }
        }
    }

    companion object {
        private const val RECENT_PRODUCT_COUNT = 10
    }
}
