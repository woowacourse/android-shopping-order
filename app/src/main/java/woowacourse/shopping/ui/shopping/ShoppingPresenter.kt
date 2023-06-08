package woowacourse.shopping.ui.shopping

import java.util.concurrent.CompletableFuture
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
    init {
        productRepository.clearCache()
    }

    override fun fetchCartCounts() {
        CompletableFuture.supplyAsync {
            cartRepository.getAll()
        }.thenAccept { result ->
            result.onSuccess { carts ->
                val cartCounts = carts.toList().associateBy({ it.product.id }, { it.quantity })
                view.setCartProducts(cartCounts)
                fetchTotalCount()
            }.onFailure { throwable -> LogUtil.logError(throwable) }
        }
    }

    override fun fetchNextProducts() {
        CompletableFuture.supplyAsync {
            productRepository.getNext(PRODUCT_PAGE_SIZE)
        }.thenAccept { result ->
            result.onSuccess { products -> view.setMoreProducts(products.map { it.toUIModel() }) }
                .onFailure { throwable -> LogUtil.logError(throwable) }
        }
    }

    override fun fetchRecentProducts() {
        val recentProducts = recentRepository.getRecent(RECENT_PRODUCT_COUNT)
            .map { it.toUIModel() }
        view.setRecentProducts(recentProducts)
    }

    override fun fetchTotalCount() {
        view.setToolbar(
            cartRepository.getTotalCheckedQuantity()
        )
    }

    override fun updateItemCount(productId: Int, count: Int) {
        CompletableFuture.supplyAsync {
            cartRepository.updateCountWithProductId(productId, count)
            cartRepository.getAll()
        }.thenAccept { result ->
            result.onSuccess { fetchTotalCount() }
                .onFailure { throwable -> LogUtil.logError(throwable) }
        }
    }

    override fun processToItemDetail(productId: Int) {
        view.navigateToProductDetail(productId)
    }

    override fun processToOrderHistories() {
        view.navigateToOrderHistories()
    }

    companion object {
        private const val PRODUCT_PAGE_SIZE = 10
        private const val RECENT_PRODUCT_COUNT = 10
    }
}
