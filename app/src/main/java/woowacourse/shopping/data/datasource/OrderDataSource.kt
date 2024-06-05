package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.remote.dto.request.RequestOrderPostDto

interface OrderDataSource {
    fun postOrder(request: RequestOrderPostDto)
}
