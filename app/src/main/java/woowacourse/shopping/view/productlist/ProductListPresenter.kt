package woowacourse.shopping.view.productlist

import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.pagination.ProductListPagination
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

    private val productsListItems = mutableListOf<ProductListViewItem>()
    private val pagination = ProductListPagination(PAGINATION_SIZE, productRepository)

    override fun fetchProducts() {
        // 최근 본 상품
        addViewedProductsItem()
        // 상품 리스트
        pagination.fetchNextItems { productsWithCartInfo ->
            addProductsItem(productsWithCartInfo.map { it.toUiModel() })
            addShowMoreItem()
            view.showProducts(productsListItems)
            view.stopLoading()
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
        val recentViewedItemSize =
            productsListItems.filterIsInstance<ProductListViewItem.RecentViewedItem>().size
        val productsItemSize =
            productsListItems.filterIsInstance<ProductListViewItem.ProductItem>().size

        val position = productsItemSize + recentViewedItemSize
        pagination.fetchNextItems { productsWithCartInfo ->
            if (productsWithCartInfo.isEmpty()) return@fetchNextItems
            productsListItems.removeLast()
            addProductsItem(productsWithCartInfo.map { it.toUiModel() })
            addShowMoreItem()
            view.notifyAddProducts(position, productsWithCartInfo.size)
        }
    }

    override fun insertCartProduct(productId: Int) {
        cartRepository.insert(productId, 1) {
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
        cartRepository.getAll {
            view.showCartCount(it.size)
        }
    }

    override fun fetchProductsCounts() {
        cartRepository.getAll { cartProducts ->
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
                view.notifyDataChanged(position)
            }
        }
    }

    override fun fetchProductCount(id: Int) {
        if (id == -1) return
        productRepository.getProductById(id) { productWithCartInfo ->
            val position =
                productsListItems.indexOfFirst { it is ProductListViewItem.ProductItem && it.product.id == id }
            productsListItems[position] = ProductListViewItem.ProductItem(
                (productsListItems[position] as ProductListViewItem.ProductItem).product.copy(
                    cartId = productWithCartInfo.cartItem?.id,
                    quantity = productWithCartInfo.cartItem?.quantity ?: 0,
                ),
            )
            view.notifyDataChanged(position)
        }
    }

    override fun updateRecentViewed(id: Int) {
        if (id == -1) return
        if (isExistRecentViewed()) productsListItems.removeIf { it is ProductListViewItem.RecentViewedItem }

        recentViewedRepository.findAll { products ->
            cartRepository.getAll { cartProducts ->
                productsListItems.add(
                    0,
                    ProductListViewItem.RecentViewedItem(products.toUiModels(cartProducts)),
                )
                view.notifyRecentViewedChanged()
            }
        }
    }

    private fun isExistRecentViewed(): Boolean =
        productsListItems[0] is ProductListViewItem.RecentViewedItem

    private fun List<Product>.toUiModels(cartProducts: List<CartProduct>): List<ProductModel> {
        return this.map { product ->
            val cartProduct = cartProducts.find { it.product.id == product.id }
            product.toUiModel(
                cartProduct?.id,
                cartProduct?.quantity ?: 0,
            )
        }
    }

    companion object {
        private const val PAGINATION_SIZE = 20
    }
}
