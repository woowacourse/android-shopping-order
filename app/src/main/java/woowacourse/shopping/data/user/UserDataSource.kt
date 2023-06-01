package woowacourse.shopping.data.user

import woowacourse.shopping.data.common.BaseResponse

interface UserDataSource {
    fun getPoint(onSuccess: (BaseResponse<PointResponse>) -> Unit, onFailure: () -> Unit)
}
