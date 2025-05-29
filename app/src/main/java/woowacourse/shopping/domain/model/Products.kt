package woowacourse.shopping.domain.model

import woowacourse.shopping.domain.model.Page.Companion.EMPTY_PAGE
import woowacourse.shopping.domain.model.Product.Companion.MINIMUM_QUANTITY

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

    fun toggleSelectionByCartId(cartId: Long): Products =
        copy(
            products =
                products.map { product ->
                    if (product.cartId == cartId) {
                        product.toggleSelection()
                    } else {
                        product
                    }
                },
        )

    fun getSelectedCartProductsPrice(): Int = products.filter { it.isSelected }.sumOf { it.productDetail.price * it.quantity }

    fun getSelectedCartRecommendProductsPrice(): Int =
        products.filter { it.quantity > MINIMUM_QUANTITY }.sumOf { it.productDetail.price * it.quantity }

    fun updateAllSelection(): Products = copy(products = products.map { it.copy(isSelected = !isAllSelected) })

    fun getSelectedCartProductIds(): List<Long> = products.filter { it.isSelected }.map { it.productDetail.id }

    fun getSelectedCartProductQuantity(): Int = products.filter { it.isSelected }.sumOf { it.quantity }

    fun getSelectedCartRecommendProductIds(): List<Long> = products.filter { it.quantity > MINIMUM_QUANTITY }.map { it.productDetail.id }

    fun getSelectedCartRecommendProductQuantity(): Int = products.filter { it.quantity > MINIMUM_QUANTITY }.sumOf { it.quantity }

    companion object {
        val EMPTY_PRODUCTS = Products(emptyList(), EMPTY_PAGE)
    }
}
