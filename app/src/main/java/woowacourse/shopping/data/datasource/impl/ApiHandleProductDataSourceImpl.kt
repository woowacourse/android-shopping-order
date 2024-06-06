package woowacourse.shopping.data.datasource.impl

import woowacourse.shopping.data.datasource.ApiHandleProductDataSource
import woowacourse.shopping.data.datasource.ProductDataSource
import woowacourse.shopping.data.remote.api.ApiResult
import woowacourse.shopping.data.remote.api.ShoppingRetrofit
import woowacourse.shopping.data.remote.api.handleApi
import woowacourse.shopping.data.remote.dto.response.ResponseProductIdGetDto
import woowacourse.shopping.data.remote.dto.response.ResponseProductsGetDto

class ApiHandleProductDataSourceImpl : ApiHandleProductDataSource {
    override suspend fun getProductsByOffset(
        page: Int,
        size: Int,
    ): ApiResult<ResponseProductsGetDto> =
        handleApi { ShoppingRetrofit.productService.getProductsByOffset(page = page, size = size) }

    override suspend fun getProductsById(id: Long): ApiResult<ResponseProductIdGetDto> =
        handleApi { ShoppingRetrofit.productService.getProductsById(id = id) }
}
