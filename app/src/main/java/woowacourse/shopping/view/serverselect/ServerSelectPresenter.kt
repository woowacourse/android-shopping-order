package woowacourse.shopping.view.serverselect

import woowacourse.shopping.domain.repository.ServerStoreRespository

class ServerSelectPresenter(private val view: ServerSelectContract.View, private val serverStoreRespository: ServerStoreRespository) : ServerSelectContract.Presenter {
    override fun selectServer(server: Server) {
        serverStoreRespository.setServerUrl(server.url)
        view.executeActivity()
    }
}
