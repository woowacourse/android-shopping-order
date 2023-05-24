package woowacourse.shopping.data.repository

import com.example.domain.repository.LoginRepository
import woowacourse.shopping.data.datasource.local.AuthInfoDataSource
import woowacourse.shopping.data.datasource.remote.LoginDataSource

class LoginRepositoryImpl(
    private val authInfoDataSource: AuthInfoDataSource,
    private val loginDataSource: LoginDataSource,
) : LoginRepository {
    override fun postAuthInfo() {
        //  step2 서버 토큰 받으면 저장하기
    }
}
