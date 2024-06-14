package woowacourse.shopping.data.datasource.impl

import woowacourse.shopping.data.datasource.ProductDataSource
import woowacourse.shopping.data.remote.api.ShoppingRetrofit
import woowacourse.shopping.data.remote.api.handleApi
import woowacourse.shopping.data.remote.dto.response.ResponseProductIdGetDto
import woowacourse.shopping.data.remote.dto.response.ResponseProductsGetDto
import woowacourse.shopping.domain.result.DataError
import woowacourse.shopping.domain.result.Result

class RemoteProductDataSource : ProductDataSource {
    override suspend fun getProductsByOffset(
        page: Int,
        size: Int,
    ): Result<ResponseProductsGetDto, DataError> =
        handleApi { ShoppingRetrofit.productService.getProductsByOffset(page = page, size = size) }

    override suspend fun getProductsById(id: Long): Result<ResponseProductIdGetDto, DataError> =
        handleApi { ShoppingRetrofit.productService.getProductsById(id = id) }
}
