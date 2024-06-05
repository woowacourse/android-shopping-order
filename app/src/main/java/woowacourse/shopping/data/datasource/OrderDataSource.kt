package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.dto.request.RequestOrderPostDto

interface OrderDataSource {
    fun postOrder(request: RequestOrderPostDto)
}
