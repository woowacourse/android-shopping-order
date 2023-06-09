package woowacourse.shopping.ui.serverselection

import woowacourse.shopping.data.remote.RemoteHost
import woowacourse.shopping.data.remote.ServerConfiguration

class ServerSelectionPresenter(
    val view: ServerSelectionContract.View
) : ServerSelectionContract.Presenter {
    override fun selectServer(serverId: Int) {
        ServerConfiguration.host = RemoteHost.values()[serverId]
        view.showProductListView()
    }
}
