package woowacourse.shopping.feature.serversetting

import woowacourse.shopping.data.preferences.UserStore
import woowacourse.shopping.module.server.Server
import woowacourse.shopping.module.server.ServerInfo

class ServerSettingPresenter(
    private val view: ServerContract.View,
    private val userStore: UserStore
) : ServerContract.Presenter {
    override fun selectDeeTooServer() {
        selectServer(Server.Deetoo)
    }

    override fun selectEmilServer() {
        selectServer(Server.Emil)
    }

    override fun selectRoiseServer() {
        selectServer(Server.Roise)
    }

    private fun selectServer(server: Server) {
        ServerInfo.changeServer(server)
        userStore.token = server.token
        view.showMainScreen(server.name)
    }
}
