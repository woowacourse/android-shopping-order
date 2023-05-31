package woowacourse.shopping.data.user

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import woowacourse.shopping.data.entity.UserEntity
import woowacourse.shopping.data.entity.UserEntity.Companion.toDomain
import woowacourse.shopping.domain.user.User

class UserRemoteSource(retrofit: Retrofit) : UserDataSource {
    private val userService = retrofit.create(UserRetrofitService::class.java)
    override fun save(user: User) {
    }

    override fun findAll(onFinish: (List<User>) -> Unit) {
        userService.selectUsers().enqueue(object : Callback<List<UserEntity>> {
            override fun onResponse(
                call: Call<List<UserEntity>>,
                response: Response<List<UserEntity>>
            ) {
                if (response.code() != 200) return
                onFinish(response.body()?.map { it.toDomain() } ?: return)
            }

            override fun onFailure(call: Call<List<UserEntity>>, t: Throwable) {
            }
        })
    }
}
