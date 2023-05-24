package woowacourse.shopping.view.productlist

import android.util.Log
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
                addProductsItem(products.toUiModels())
                // 더보기
                addShowMoreItem()
                mark += products.size
            }
        }
    }

    private fun addShowMoreItem() {
        productRepository.isExistByMark(mark) { isNextExist ->
            if (isNextExist) productsListItems.add(ProductListViewItem.ShowMoreItem())
            view.showProducts(productsListItems)
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
        productRepository.getProductsByRange(mark, PAGINATION_SIZE) {
            val nextProducts = it.toUiModels()
            productsListItems.removeLast()
            addProductsItem(nextProducts)
            mark += nextProducts.size
            productRepository.isExistByMark(mark) { isNextExist ->
                if (isNextExist) productsListItems.add(ProductListViewItem.ShowMoreItem())
                view.notifyAddProducts(position, nextProducts.size)
            }
        }
    }

    override fun addToCartProducts(id: Int, count: Int) {
        cartRepository.add(id, count)
        fetchProductCount(id)
    }

    override fun updateCartProductCount(id: Int, count: Int) {
        if (count == 0) {
            cartRepository.remove(id)
            fetchProductCount(id)
            fetchCartCount()
            return
        }
        cartRepository.update(id, count)
        fetchProductCount(id)
    }

    override fun fetchCartCount() {
        view.showCartCount(cartRepository.findAll().size)
    }

    override fun fetchProductCounts() {
        val cartProducts = cartRepository.findAll()

        val itemsHaveCount = productsListItems
            .asSequence()
            .filterIsInstance<ProductListViewItem.ProductItem>()
            .filter { it.product.count > 0 }
            .toList()

        itemsHaveCount.forEach { item ->
            val cartProduct = cartProducts.find { it.id == item.product.id }
            item.product.count = cartProduct?.count ?: 0
            view.notifyDataChanged(productsListItems.indexOf(item))
        }
    }

    override fun fetchProductCount(id: Int) {
        if (id == -1) return
        val product = cartRepository.find(id)
        val item = productsListItems.filterIsInstance<ProductListViewItem.ProductItem>()
            .filter { it.product.id == id }[0]
        item.product.count = product?.count ?: 0
        view.notifyDataChanged(productsListItems.indexOf(item))
    }

    override fun updateRecentViewed(id: Int) {
        if (id == -1) return
        if (isExistRecentViewed()) productsListItems.removeIf { it is ProductListViewItem.RecentViewedItem }

        recentViewedRepository.findAll {
            productsListItems.add(
                0,
                ProductListViewItem.RecentViewedItem(it.toUiModels()),
            )
            view.notifyRecentViewedChanged()
            Log.d("NOTIFY", true.toString())
        }
    }

    private fun isExistRecentViewed(): Boolean =
        productsListItems[0] is ProductListViewItem.RecentViewedItem

    private fun List<Product>.toUiModels(): List<ProductModel> {
        return this.map { it.toUiModel(cartRepository.find(it.id)?.count ?: 0) }
    }

    companion object {
        private const val PAGINATION_SIZE = 20
    }
}
