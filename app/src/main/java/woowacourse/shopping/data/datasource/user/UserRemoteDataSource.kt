package woowacourse.shopping.data.datasource.user

import woowacourse.shopping.data.model.DataUser

interface UserRemoteDataSource {

    fun getUser(onReceived: (user: DataUser) -> Unit)
}
