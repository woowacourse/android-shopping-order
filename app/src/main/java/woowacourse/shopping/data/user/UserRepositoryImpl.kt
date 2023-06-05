package woowacourse.shopping.data.user

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.User
import woowacourse.shopping.data.HttpErrorHandler
import woowacourse.shopping.data.common.model.BaseResponse
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.data.user.model.UserDataModel
import woowacourse.shopping.repository.UserRepository

class UserRepositoryImpl constructor(
    private val userRemoteDataSource: UserRemoteDataSource,
    private val httpErrorHandler: HttpErrorHandler,
) : UserRepository {
    override fun getUserPoint(onSuccess: (User?) -> Unit) {
        userRemoteDataSource.getUserPoint().enqueue(object : Callback<BaseResponse<UserDataModel>> {
            override fun onResponse(
                call: Call<BaseResponse<UserDataModel>>,
                response: Response<BaseResponse<UserDataModel>>
            ) {
                val userDataModel = response.body()?.result
                val user = userDataModel?.toDomain()
                onSuccess(user)
            }

            override fun onFailure(call: Call<BaseResponse<UserDataModel>>, t: Throwable) {
                httpErrorHandler.handleHttpError(t)
            }
        })
    }
}
