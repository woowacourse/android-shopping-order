package woowacourse.shopping.data.datasource

import android.os.Handler
import woowacourse.shopping.data.dto.request.RequestCartItemPostDto
import woowacourse.shopping.data.dto.response.ResponseCartItemsGetDto
import woowacourse.shopping.data.dto.response.ResponseProductIdGetDto
import woowacourse.shopping.data.dto.response.ResponseProductsGetDto

interface ProductDataSource {

    fun getProductsByOffset(
        page: Int,
        size: Int,
    ): ResponseProductsGetDto?

    fun getProductsById(id: Long): ResponseProductIdGetDto?
}
