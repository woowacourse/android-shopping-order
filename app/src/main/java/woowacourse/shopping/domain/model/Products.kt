package woowacourse.shopping.domain.model

import woowacourse.shopping.domain.model.Page.Companion.EMPTY_PAGE

data class Products(
    val products: List<Product>,
    val page: Page = EMPTY_PAGE,
) {
    val isAllSelected: Boolean get() = products.all { it.isSelected }

    operator fun plus(other: Products): Products {
        val mergedProducts = products + other.products
        return Products(
            products = mergedProducts,
            page = other.page,
        )
    }

    fun updateProductQuantity(
        productId: Long,
        quantity: Int,
    ): Products {
        val updatedProducts =
            products.map { product ->
                if (product.productDetail.id == productId) {
                    product.copy(quantity = quantity)
                } else {
                    product
                }
            }
        return copy(products = updatedProducts)
    }

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

    fun updateSelectionByProductId(cartId: Long): Products =
        copy(
            products =
                products.map { product ->
                    if (product.cartId == cartId) {
                        product.updateSelection()
                    } else {
                        product
                    }
                },
        )

    fun toggleAllSelection(): Products = copy(products = products.map { it.copy(isSelected = !isAllSelected) })

    fun getSelectedProductsPrice(): Int = products.filter { it.isSelected }.sumOf { it.productDetail.price * it.quantity }

    fun getSelectedProductIds(): List<Long> = products.filter { it.isSelected }.map { it.productDetail.id }

    fun getSelectedCartProductIds(): Set<Long> = products.filter { it.isSelected }.mapNotNull { it.cartId }.toSet()

    fun getSelectedProductsQuantity(): Int = products.filter { it.isSelected }.sumOf { it.quantity }

    companion object {
        val EMPTY_PRODUCTS = Products(emptyList(), EMPTY_PAGE)
    }
}
