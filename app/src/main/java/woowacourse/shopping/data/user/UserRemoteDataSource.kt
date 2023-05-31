package woowacourse.shopping.data.user

import retrofit2.Call
import woowacourse.shopping.data.common.model.BaseResponse
import woowacourse.shopping.data.user.model.UserDataModel

interface UserRemoteDataSource {

    fun getUserPoint(): Call<BaseResponse<UserDataModel>>
}
