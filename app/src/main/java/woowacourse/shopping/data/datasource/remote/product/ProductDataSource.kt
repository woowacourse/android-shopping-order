package woowacourse.shopping.data.datasource.remote.product

import com.example.domain.util.CustomResult
import woowacourse.shopping.data.remote.response.ProductResponseDto

interface ProductDataSource {

    fun getSubListProducts(limit: Int, scrollCount: Int): Result<List<ProductResponseDto>>

    fun getProducts(
        limit: Int,
        scrollCount: Int,
        onSuccess: (CustomResult<List<ProductResponseDto>>) -> Unit,
        onFailure: (CustomResult<Error>) -> Unit,
    )
}
