package woowacourse.shopping.domain.product

class PagedProducts(
    val products: List<Product>,
    val loadable: Boolean,
) {
    companion object {
        fun from(
            products: List<Product>,
            pageNumber: Int?,
            totalPages: Int?,
        ): PagedProducts =
            if (pageNumber == null || totalPages == null) {
                PagedProducts(products, false)
            } else {
                PagedProducts(products, pageNumber + 1 < totalPages)
            }
    }
}
