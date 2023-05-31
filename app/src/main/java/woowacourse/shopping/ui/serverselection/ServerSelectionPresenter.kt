package woowacourse.shopping.ui.serverselection

import woowacourse.shopping.network.RemoteHost
import woowacourse.shopping.network.ServerConfiguration

class ServerSelectionPresenter(
    val view: ServerSelectionContract.View
) : ServerSelectionContract.Presenter {
    override fun selectServer(serverId: Int) {
        ServerConfiguration.host = RemoteHost.values()[serverId]
        view.showProductListView()
    }
}
