package woowacourse.shopping.view.productlist

import woowacourse.shopping.data.pagination.ProductListPagination
import woowacourse.shopping.data.remote.result.DataResult
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.data.repository.RecentViewedRepository
import woowacourse.shopping.model.ProductModel
import woowacourse.shopping.model.toUiModel

class ProductListPresenter(
    private val view: ProductListContract.View,
    private val productRepository: ProductRepository,
    private val recentViewedRepository: RecentViewedRepository,
    private val cartRepository: CartRepository,
) : ProductListContract.Presenter {

    private val productsListItems = mutableListOf<ProductListViewItem>()
    private val pagination = ProductListPagination(PAGINATION_SIZE, productRepository)

    override fun fetchProducts() {
        // 최근 본 상품
        addViewedProductsItem()
        // 상품 리스트
        pagination.fetchNextItems { result ->
            when (result) {
                is DataResult.Success -> {
                    val productsWithCartInfo = result.response
                    addProductsItem(productsWithCartInfo.map { it.toUiModel() })
                    addShowMoreItem()
                    view.showProducts(productsListItems)
                    view.stopLoading()
                }
                is DataResult.Failure -> {
                    view.showServerFailureToast()
                }
                is DataResult.NotSuccessfulError -> {
                    view.showNotSuccessfulErrorToast()
                }
                is DataResult.WrongResponse -> {
                    view.showServerResponseWrongToast()
                }
            }
        }
    }

    private fun addShowMoreItem() {
        if (pagination.isNextEnabled) productsListItems.add(ProductListViewItem.ShowMoreItem())
    }

    private fun addViewedProductsItem() {
        recentViewedRepository.findAll { products ->
            if (products.isEmpty()) return@findAll
            val viewedProductsModel = products.map { it.toUiModel(null) }
            productsListItems.add(ProductListViewItem.RecentViewedItem(viewedProductsModel))
        }
    }

    private fun addProductsItem(products: List<ProductModel>) {
        productsListItems.addAll(products.map { ProductListViewItem.ProductItem(it) })
    }

    override fun showProductDetail(product: ProductModel) {
        val recentViewedItem =
            productsListItems.filterIsInstance<ProductListViewItem.RecentViewedItem>().getOrNull(0)
        var lastViewedProduct: ProductModel? = null
        if (recentViewedItem != null) {
            lastViewedProduct = recentViewedItem.products[0]
        }
        view.onClickProductDetail(product, lastViewedProduct)
    }

    override fun loadMoreProducts() {
        pagination.fetchNextItems { result ->
            when (result) {
                is DataResult.Success -> {
                    val productsWithCartInfo = result.response
                    if (productsWithCartInfo.isEmpty()) return@fetchNextItems
                    productsListItems.removeLast()
                    addProductsItem(productsWithCartInfo.map { it.toUiModel() })
                    addShowMoreItem()
                    view.changeItems(productsListItems)
                }
                is DataResult.Failure -> {
                    view.showServerFailureToast()
                }
                is DataResult.NotSuccessfulError -> {
                    view.showNotSuccessfulErrorToast()
                }
                is DataResult.WrongResponse -> {
                    view.showServerResponseWrongToast()
                }
            }
        }
    }

    override fun insertCartProduct(productId: Int) {
        cartRepository.insert(productId, 1) { result ->
            when (result) {
                is DataResult.Success -> {
                    fetchProductCount(productId)
                    fetchCartCount()
                }
                is DataResult.Failure -> {
                    view.showServerFailureToast()
                }
                is DataResult.NotSuccessfulError -> {
                    view.showNotSuccessfulErrorToast()
                }
                is DataResult.WrongResponse -> {
                    view.showServerResponseWrongToast()
                }
            }
        }
    }

    override fun updateCartProductCount(cartId: Int, productId: Int, count: Int) {
        if (count == 0) {
            cartRepository.remove(cartId) { result ->
                when (result) {
                    is DataResult.Success -> {
                        fetchProductCount(productId)
                        fetchCartCount()
                    }
                    is DataResult.Failure -> {
                        view.showServerFailureToast()
                    }
                    is DataResult.NotSuccessfulError -> {
                        view.showNotSuccessfulErrorToast()
                    }
                    is DataResult.WrongResponse -> {
                        view.showServerResponseWrongToast()
                    }
                }
            }
            return
        }
        cartRepository.update(cartId, count) { result ->
            when (result) {
                is DataResult.Success -> {
                    fetchProductCount(productId)
                }
                is DataResult.Failure -> {
                    view.showServerFailureToast()
                }
                is DataResult.NotSuccessfulError -> {
                    view.showNotSuccessfulErrorToast()
                }
                is DataResult.WrongResponse -> {
                    view.showServerResponseWrongToast()
                }
            }
        }
    }

    override fun fetchCartCount() {
        cartRepository.getAll { result ->
            when (result) {
                is DataResult.Success -> {
                    view.showCartCount(result.response.size)
                }
                is DataResult.Failure -> {
                    view.showServerFailureToast()
                }
                is DataResult.NotSuccessfulError -> {
                    view.showNotSuccessfulErrorToast()
                }
                is DataResult.WrongResponse -> {
                    view.showServerResponseWrongToast()
                }
            }
        }
    }

    override fun fetchProductsCounts() {
        cartRepository.getAll { result ->
            when (result) {
                is DataResult.Success -> {
                    val cartProducts = result.response
                    val itemsHaveCount = productsListItems
                        .asSequence()
                        .filterIsInstance<ProductListViewItem.ProductItem>()
                        .filter { it.product.quantity > 0 }
                        .toList()

                    itemsHaveCount.forEach { item ->
                        val cartProduct = cartProducts.find { it.product.id == item.product.id }
                        val position = productsListItems.indexOf(item)
                        productsListItems[position] = ProductListViewItem.ProductItem(
                            (productsListItems[position] as ProductListViewItem.ProductItem).product.copy(
                                cartId = cartProduct?.id,
                                quantity = cartProduct?.quantity ?: 0,
                            ),
                        )
                        view.changeItems(productsListItems)
                    }
                }
                is DataResult.Failure -> {
                    view.showServerFailureToast()
                }
                is DataResult.NotSuccessfulError -> {
                    view.showNotSuccessfulErrorToast()
                }
                is DataResult.WrongResponse -> {
                    view.showServerResponseWrongToast()
                }
            }
        }
    }

    override fun fetchProductCount(id: Int) {
        if (id == -1) return
        productRepository.getProductById(id) { result ->
            when (result) {
                is DataResult.Success -> {
                    val productWithCartInfo = result.response
                    val position =
                        productsListItems.indexOfFirst { it is ProductListViewItem.ProductItem && it.product.id == id }
                    productsListItems[position] = ProductListViewItem.ProductItem(
                        (productsListItems[position] as ProductListViewItem.ProductItem).product.copy(
                            cartId = productWithCartInfo.cartItem?.id,
                            quantity = productWithCartInfo.cartItem?.quantity ?: 0,
                        ),
                    )
                    view.changeItems(productsListItems)
                }
                is DataResult.Failure -> {
                    view.showServerFailureToast()
                }
                is DataResult.NotSuccessfulError -> {
                    view.showNotSuccessfulErrorToast()
                }
                is DataResult.WrongResponse -> {
                    view.showServerResponseWrongToast()
                }
            }
        }
    }

    override fun updateRecentViewed(id: Int) {
        if (id == -1) return
        productsListItems.removeIf { it is ProductListViewItem.RecentViewedItem }

        recentViewedRepository.findAll { products ->
            productsListItems.add(
                0,
                ProductListViewItem.RecentViewedItem(products.map { it.toUiModel(null) }),
            )
            view.changeItems(productsListItems)
        }
    }

    companion object {
        private const val PAGINATION_SIZE = 20
    }
}
