package woowacourse.shopping.ui.shopping

import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.data.repository.RecentRepository
import woowacourse.shopping.mapper.toUIModel
import woowacourse.shopping.utils.LogUtil

class ShoppingPresenter(
    private val view: ShoppingContract.View,
    private val productRepository: ProductRepository,
    private val recentRepository: RecentRepository,
    private val cartRepository: CartRepository
) : ShoppingContract.Presenter {
    override fun setUpProducts() {
        productRepository.clearCache()
        setUpCartCounts()
        setUpNextProducts()
        setUpRecentProducts()
    }

    override fun setUpCartCounts() {
        cartRepository.getAll { result ->
            result.onSuccess { carts ->
                val cartProducts = carts.all().associateBy({ it.product.id }, { it.quantity })
                view.setCartProducts(cartProducts)
                setUpTotalCount()
            }.onFailure { throwable -> LogUtil.logError(throwable) }
        }
    }

    override fun setUpNextProducts() {
        productRepository.getNext(PRODUCT_PAGE_SIZE) { result ->
            result.onSuccess { products -> view.addMoreProducts(products.map { it.toUIModel() }) }
                .onFailure { throwable -> LogUtil.logError(throwable) }
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
        cartRepository.updateCountWithProductId(productId, count) {
            cartRepository.getAll {
                setUpTotalCount()
            }
        }
    }

    override fun navigateToItemDetail(productId: Int) {
        productRepository.findById(productId) { result ->
            result.onSuccess { view.navigateToProductDetail(it.toUIModel()) }
                .onFailure { throwable -> LogUtil.logError(throwable) }
        }
    }

    companion object {
        private const val PRODUCT_PAGE_SIZE = 2
        private const val RECENT_PRODUCT_COUNT = 10
    }
}
