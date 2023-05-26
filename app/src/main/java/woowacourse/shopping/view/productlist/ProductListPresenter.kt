package woowacourse.shopping.view.productlist

import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentViewedRepository
import woowacourse.shopping.model.ProductModel
import woowacourse.shopping.model.toUiModel

class ProductListPresenter(
    private val view: ProductListContract.View,
    private val productRepository: ProductRepository,
    private val recentViewedRepository: RecentViewedRepository,
    private val cartRepository: CartRepository,
) : ProductListContract.Presenter {
    private var mark: Int = 0

    private val productsListItems = mutableListOf<ProductListViewItem>()

    override fun fetchProducts() {
        // 최근 본 항목
        recentViewedRepository.findAll { viewedProducts ->
            if (viewedProducts.isNotEmpty()) addViewedProductsItem(viewedProducts)
            // 상품 리스트
            productRepository.getProductsByRange(mark, PAGINATION_SIZE) { products ->
                cartRepository.findAll {
                    addProductsItem(products.toUiModels(it))
                    mark += products.size
                    // 더보기
                    addShowMoreItem()
                }
            }
        }
    }

    private fun addShowMoreItem() {
        productRepository.isExistByMark(mark) { isNextExist ->
            if (isNextExist) productsListItems.add(ProductListViewItem.ShowMoreItem())
            view.showProducts(productsListItems)
            view.stopLoading()
        }
    }

    private fun addViewedProductsItem(products: List<Product>) {
        val viewedProductsModel = products.map { it.toUiModel() }
        productsListItems.add(ProductListViewItem.RecentViewedItem(viewedProductsModel))
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
        val recentViewedItemSize =
            productsListItems.filterIsInstance<ProductListViewItem.RecentViewedItem>().size
        val productsItemSize =
            productsListItems.filterIsInstance<ProductListViewItem.ProductItem>().size

        val position = productsItemSize + recentViewedItemSize
        productRepository.getProductsByRange(mark, PAGINATION_SIZE) { products ->
            cartRepository.findAll { cartProducts ->
                val nextProducts = products.toUiModels(cartProducts)
                productsListItems.removeLast()
                addProductsItem(nextProducts)
                mark += nextProducts.size
                productRepository.isExistByMark(mark) { isNextExist ->
                    if (isNextExist) productsListItems.add(ProductListViewItem.ShowMoreItem())
                    view.notifyAddProducts(position, nextProducts.size)
                }
            }
        }
    }

    override fun insertCartProduct(productId: Int) {
        cartRepository.insert(productId) {
            fetchProductCount(productId)
        }
    }

    override fun updateCartProductCount(cartId: Int, productId: Int, count: Int) {
        if (count == 0) {
            cartRepository.remove(cartId) {
                fetchProductCount(productId)
                fetchCartCount()
            }
            return
        }
        cartRepository.update(cartId, count) {
            fetchProductCount(productId)
        }
    }

    override fun fetchCartCount() {
        cartRepository.findAll { view.showCartCount(it.size) }
    }

    override fun fetchProductsCounts() {
        cartRepository.findAll { cartProducts ->
            val itemsHaveCount = productsListItems
                .asSequence()
                .filterIsInstance<ProductListViewItem.ProductItem>()
                .filter { it.product.count > 0 }
                .toList()

            itemsHaveCount.forEach { item ->
                val cartProduct = cartProducts.find { it.cartId == item.product.id }
                val position = productsListItems.indexOf(item)
                productsListItems[position] = ProductListViewItem.ProductItem(
                    (productsListItems[position] as ProductListViewItem.ProductItem).product.copy(
                        cartId = cartProduct?.cartId ?: 0,
                        count = cartProduct?.count ?: 0,
                    ),
                )
                view.notifyDataChanged(position)
            }
        }
    }

    override fun fetchProductCount(id: Int) {
        if (id == -1) return
        cartRepository.findAll { cartProducts ->
            val cartProduct = cartProducts.find { it.product.id == id }
            val position =
                productsListItems.indexOfFirst { it is ProductListViewItem.ProductItem && it.product.id == id }
            productsListItems[position] = ProductListViewItem.ProductItem(
                (productsListItems[position] as ProductListViewItem.ProductItem).product.copy(
                    cartId = cartProduct?.cartId ?: 0,
                    count = cartProduct?.count ?: 0,
                ),
            )
            view.notifyDataChanged(position)
        }
    }

    override fun updateRecentViewed(id: Int) {
        if (id == -1) return
        if (isExistRecentViewed()) productsListItems.removeIf { it is ProductListViewItem.RecentViewedItem }

        recentViewedRepository.findAll { products ->
            cartRepository.findAll { cartProducts ->
                productsListItems.add(
                    0,
                    ProductListViewItem.RecentViewedItem(products.toUiModels(cartProducts)),
                )
            }
            view.notifyRecentViewedChanged()
        }
    }

    private fun isExistRecentViewed(): Boolean =
        productsListItems[0] is ProductListViewItem.RecentViewedItem

    private fun List<Product>.toUiModels(cartProducts: List<CartProduct>): List<ProductModel> {
        return this.map { product ->
            val cartProduct = cartProducts.find { it.product.id == product.id }
            product.toUiModel(
                cartProduct?.cartId ?: 0,
                cartProduct?.count ?: 0,
            )
        }
    }

    companion object {
        private const val PAGINATION_SIZE = 20
    }
}
