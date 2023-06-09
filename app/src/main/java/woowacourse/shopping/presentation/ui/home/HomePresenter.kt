package woowacourse.shopping.presentation.ui.home

import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.Operator
import woowacourse.shopping.domain.model.RecentlyViewedProduct
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentlyViewedRepository
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.presentation.model.HomeData
import woowacourse.shopping.presentation.model.HomeMapper.toProductItem
import woowacourse.shopping.presentation.model.ProductItem
import woowacourse.shopping.presentation.model.RecentlyViewedItem
import woowacourse.shopping.presentation.model.ShowMoreItem
import woowacourse.shopping.presentation.ui.home.adapter.HomeViewType.PRODUCT
import woowacourse.shopping.presentation.ui.home.adapter.HomeViewType.SHOW_MORE

class HomePresenter(
    private val view: HomeContract.View,
    private val productRepository: ProductRepository,
    private val recentlyViewedRepository: RecentlyViewedRepository,
    private val shoppingCartRepository: ShoppingCartRepository,
) : HomeContract.Presenter {
    private var lastProductId: Long = 0
    private val homeData = mutableListOf<HomeData>()
    private var recentlyViewedItem = listOf<RecentlyViewedProduct>()

    override fun setHome() {
        fetchFirstProducts()
        fetchTotalQuantity()
    }

    override fun fetchRecentlyViewed() {
        recentlyViewedItem = recentlyViewedRepository.getRecentlyViewedProducts(UNIT)
        if (recentlyViewedItem.isEmpty()) return
        checkRecentlyViewedInit()
        view.updateRecentlyViewedProducts(recentlyViewedItem.toList())
    }

    private fun fetchProducts(callback: (Result<List<CartProduct>>, Boolean) -> Unit) {
        productRepository.fetchPagedProducts(
            pageItemCount = UNIT,
            lastId = lastProductId,
            callback = callback,
        )
    }

    private fun fetchFirstProducts() {
        val callback: (Result<List<CartProduct>>, Boolean) -> Unit = { result, isLast ->
            result
                .onSuccess {
                    val products = it.map { it.toProductItem() }
                    lastProductId = products.lastOrNull()?.productId ?: lastProductId
                    homeData.addAll(products)
                    view.setHomeData(homeData)
                    checkIsLastProduct(isLast)
                    view.notifyLoadingFinished()
                }
                .onFailure { view.showError(it.message ?: "에러 메시지가 없습니다.") }
        }
        fetchProducts(callback)
    }

    override fun fetchMoreProducts() {
        deleteShowMoreItem()
        val startPosition = homeData.size
        val callback: (Result<List<CartProduct>>, Boolean) -> Unit = { result, isLast ->
            result
                .onSuccess {
                    val products = it.map { it.toProductItem() }
                    lastProductId = products.lastOrNull()?.productId ?: lastProductId
                    homeData.addAll(products)
                    view.appendProductItems(startPosition, products.size)
                    checkIsLastProduct(isLast)
                    view.notifyLoadingFinished()
                }
                .onFailure { view.showError(it.message ?: "에러 메시지가 없습니다.") }
        }
        fetchProducts(callback)
    }

    private fun checkIsLastProduct(isLast: Boolean) {
        if (!isLast) addShowMoreItem()
    }

    private fun checkRecentlyViewedInit() {
        if (homeData.isEmpty() || (homeData[0] !is RecentlyViewedItem)) {
            homeData.add(0, RecentlyViewedItem(recentlyViewedItem))
            view.initRecentlyViewed()
        }
    }

    private fun addShowMoreItem() {
        homeData.add(ShowMoreItem())
        view.appendShowMoreItem(homeData.size - 1)
    }

    private fun deleteShowMoreItem() {
        if (homeData.isNotEmpty() && homeData.last().viewType == SHOW_MORE) {
            homeData.removeLast()
            view.removeShowMoreItem(homeData.size)
        }
    }

    override fun updateProductQuantity(position: Int, operator: Operator) {
        val cartProduct: CartProduct = updatedQuantity(position, operator) ?: return
        if (cartProduct.cartItem.quantity == 0) {
            deleteProductInCart(position, cartProduct)
            return
        }
        if (operator == Operator.INCREASE && cartProduct.cartItem.quantity == 1) {
            putProductInCart(position, cartProduct)
            return
        }
        updateProductItem(position, cartProduct)
    }

    private fun updatedQuantity(position: Int, operator: Operator): CartProduct? {
        if (homeData[position].viewType != PRODUCT) {
            view.showError("viewType이 맞지 않음")
            return null
        }
        return operator.operate((homeData[position] as ProductItem).cartProduct)
    }

    private fun putProductInCart(position: Int, cartProduct: CartProduct) {
        val callback: (Result<Long>) -> Unit = { result ->
            result
                .onSuccess {
                    val cartItem = cartProduct.cartItem.copy(id = it)
                    homeData[position] = ProductItem(cartProduct.copy(cartItem = cartItem))
                    view.updateProductQuantity(position)
                }
                .onFailure { view.showError(it.message ?: "에러 메시지가 없습니다.") }
        }

        shoppingCartRepository.insert(
            callback = callback,
            productId = cartProduct.product.id,
            quantity = cartProduct.cartItem.quantity,
        )
    }

    private fun updateProductItem(position: Int, cartProduct: CartProduct) {
        val callback: (Result<Boolean>) -> Unit = { result ->
            result
                .onSuccess {
                    homeData[position] = ProductItem(cartProduct)
                    view.updateProductQuantity(position)
                }
                .onFailure { view.showError(it.message ?: "에러 메시지가 없습니다.") }
        }

        shoppingCartRepository.update(
            id = cartProduct.cartItem.id,
            updatedQuantity = cartProduct.cartItem.quantity,
            callback = callback,
        )
    }

    private fun deleteProductInCart(position: Int, cartProduct: CartProduct) {
        val callback: (Result<Boolean>) -> Unit = { result ->
            result
                .onSuccess {
                    if (it) {
                        homeData[position] = ProductItem(cartProduct)
                        view.updateProductQuantity(position)
                    } else {
                        view.showError("서버통신에 성공했지만 오류가 발생했습니다.")
                    }
                }
                .onFailure { view.showError(it.message ?: "에러 메시지가 없습니다.") }
        }
        shoppingCartRepository.delete(
            id = (homeData[position] as ProductItem).cartId,
            callback = callback,
        )
    }

    override fun fetchTotalQuantity() {
        shoppingCartRepository.fetchAll { result ->
            result
                .onSuccess { view.updateTotalQuantity(it.size) }
                .onFailure { view.showError(it.message ?: "에러 메시지가 없습니다.") }
        }
    }

    companion object {
        private const val UNIT = 10
    }
}
