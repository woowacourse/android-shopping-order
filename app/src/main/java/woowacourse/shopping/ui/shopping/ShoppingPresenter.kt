package woowacourse.shopping.ui.shopping

import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.data.repository.RecentRepository
import woowacourse.shopping.mapper.toUIModel
import woowacourse.shopping.utils.ErrorHandler

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
        productRepository.getNext(PRODUCT_PAGE_SIZE) { result ->
            when (result.isSuccess) {
                true -> view.addMoreProducts(
                    result.getOrNull()?.map { it.toUIModel() } ?: return@getNext
                )
                false -> ErrorHandler.printError(
                    result.exceptionOrNull() ?: Exception("알 수 없는 오류가 발생했습니다.")
                )
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
            when (it.isSuccess) {
                true -> view.navigateToProductDetail(
                    it.getOrNull()?.toUIModel() ?: return@findById
                )
                false -> ErrorHandler.printError(
                    it.exceptionOrNull() ?: Exception("알 수 없는 오류가 발생했습니다.")
                )
            }
        }
    }

    companion object {
        private const val PRODUCT_PAGE_SIZE = 2
        private const val RECENT_PRODUCT_COUNT = 10
    }
}
