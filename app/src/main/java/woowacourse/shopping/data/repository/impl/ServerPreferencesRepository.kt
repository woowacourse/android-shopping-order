package woowacourse.shopping.data.repository.impl

import woowacourse.shopping.data.datasource.ServerStoreDataSource
import woowacourse.shopping.data.repository.ServerStoreRespository

class ServerPreferencesRepository(private val serverStoreDataSource: ServerStoreDataSource) :
    ServerStoreRespository {
    override fun setServerUrl(url: String) {
        serverStoreDataSource.setServerUrl(url)
    }

    override fun getServerUrl(): String = serverStoreDataSource.getServerUrl()
}
