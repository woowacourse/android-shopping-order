package woowacourse.shopping.domain.model

import woowacourse.shopping.domain.model.Page.Companion.EMPTY_PAGE

data class Products(
    val products: List<Product>,
    val page: Page = EMPTY_PAGE,
) {
    val isAllSelected: Boolean get() = products.all { it.isSelected }
    val selectedProductsQuantity: Int get() = products.filter { it.isSelected }.sumOf { it.quantity }
    val selectedProductsPrice: Int = products.filter { it.isSelected }.sumOf { it.totalPrice }

    operator fun plus(other: Products): Products {
        val mergedProducts = products + other.products
        return Products(
            products = mergedProducts,
            page = other.page,
        )
    }

    fun updateSelection(
        product: Product,
        isSelected: Boolean = !product.isSelected,
    ): Products = updateProduct(product.copy(isSelected = isSelected))

    fun updateQuantity(
        product: Product,
        quantity: Int,
    ): Products = updateProduct(product.copy(quantity = quantity))

    fun updateProduct(newProduct: Product): Products {
        val updatedProducts =
            products.map { product ->
                if (product.productDetail.id == newProduct.productDetail.id) {
                    newProduct
                } else {
                    product
                }
            }
        return copy(products = updatedProducts)
    }

    fun updateProducts(newProducts: List<Product>): Products {
        val updatedProducts =
            products.map { product ->
                newProducts.find { it.productDetail.id == product.productDetail.id } ?: product
            }
        return copy(products = updatedProducts)
    }

    fun getProductByProductId(productId: Long): Product? = products.find { it.productDetail.id == productId }

    fun getProductByCartId(cartId: Long): Product? = products.find { it.cartId == cartId }

    fun toggleAllSelection(): Products = copy(products = products.map { it.copy(isSelected = !isAllSelected) })

    fun getSelectedProductIds(): List<Long> = products.filter { it.isSelected }.map { it.productDetail.id }

    companion object {
        val EMPTY_PRODUCTS = Products(emptyList(), EMPTY_PAGE)
    }
}
