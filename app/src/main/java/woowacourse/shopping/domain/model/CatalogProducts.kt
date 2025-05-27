package woowacourse.shopping.domain.model

import woowacourse.shopping.domain.model.Page.Companion.EMPTY_PAGE

data class CatalogProducts(
    val products: List<CatalogProduct>,
    val page: Page,
) {
    val catalogProductsQuantity: Int get() = products.sumOf { it.quantity }

    operator fun plus(other: CatalogProducts): CatalogProducts {
        val mergedProducts = products + other.products
        return CatalogProducts(
            products = mergedProducts,
            page = other.page,
        )
    }

    fun updateCatalogProductQuantity(
        productId: Long,
        quantity: Int,
    ): CatalogProducts {
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

    fun updateCatalogProduct(newProduct: CatalogProduct): CatalogProducts {
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

    fun updateCatalogProducts(newProducts: List<CatalogProduct>): CatalogProducts {
        val updatedProducts =
            products.map { product ->
                newProducts.find { it.productDetail.id == product.productDetail.id } ?: product
            }
        return copy(products = updatedProducts)
    }

    companion object {
        val EMPTY_CATALOG_PRODUCTS = CatalogProducts(emptyList(), EMPTY_PAGE)
    }
}
