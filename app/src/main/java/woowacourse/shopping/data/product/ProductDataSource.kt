package woowacourse.shopping.data.product

import woowacourse.shopping.data.product.dto.ProductDetail

interface ProductDataSource {
    fun findProductById(id: Long, callback: (ProductDetail) -> Unit)
    fun getProductsWithRange(
        lastId: Long,
        pageItemCount: Int,
        callback: (List<ProductDetail>, Boolean) -> Unit,
    )
}
