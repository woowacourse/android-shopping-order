package woowacourse.shopping.domain.model

import woowacourse.shopping.domain.model.Page.Companion.EMPTY_PAGE

data class Products(
    val products: List<Product>,
    val page: Page,
) {
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

    companion object {
        val EMPTY_PRODUCTS = Products(emptyList(), EMPTY_PAGE)
    }
}
