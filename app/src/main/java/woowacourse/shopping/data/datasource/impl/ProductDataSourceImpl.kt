package woowacourse.shopping.data.datasource.impl

import woowacourse.shopping.data.datasource.ProductDataSource
import woowacourse.shopping.data.remote.dto.response.ResponseProductIdGetDto
import woowacourse.shopping.data.remote.dto.response.ResponseProductsGetDto
import woowacourse.shopping.data.remote.api.ShoppingRetrofit

class ProductDataSourceImpl : ProductDataSource {
    override fun getProductsByOffset(
        page: Int,
        size: Int,
    ): ResponseProductsGetDto? =
        ShoppingRetrofit.productService.getProductsByOffset(page = page, size = size).execute()
            .body()

    override fun getProductsById(id: Long): ResponseProductIdGetDto? =
        ShoppingRetrofit.productService.getProductsById(id = id).execute().body()
}
