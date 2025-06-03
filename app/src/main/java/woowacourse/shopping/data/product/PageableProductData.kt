package woowacourse.shopping.data.product

import woowacourse.shopping.data.product.entity.ProductEntity

class PageableProductData(
    val products: List<ProductEntity>,
    val loadable: Boolean,
) {
    companion object {
        fun from(
            products: List<ProductEntity>,
            pageNumber: Int?,
            totalPages: Int?,
        ): PageableProductData =
            if (pageNumber == null || totalPages == null) {
                PageableProductData(products, false)
            } else {
                PageableProductData(products, pageNumber + 1 < totalPages)
            }
    }
}
