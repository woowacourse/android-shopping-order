package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.model.DataUser

interface UserRemoteDataSource {

    fun getUser(onReceived: (user: DataUser) -> Unit)
}
