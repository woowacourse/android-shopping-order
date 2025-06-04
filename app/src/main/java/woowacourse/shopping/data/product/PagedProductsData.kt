package woowacourse.shopping.data.product

import woowacourse.shopping.data.product.entity.ProductEntity

class PagedProductsData(
    val products: List<ProductEntity>,
    val loadable: Boolean,
) {
    companion object {
        fun from(
            products: List<ProductEntity>,
            pageNumber: Int?,
            totalPages: Int?,
        ): PagedProductsData =
            if (pageNumber == null || totalPages == null) {
                PagedProductsData(products, false)
            } else {
                PagedProductsData(products, pageNumber + 1 < totalPages)
            }
    }
}
