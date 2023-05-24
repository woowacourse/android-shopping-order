package woowacourse.shopping.ui.serverselection

import woowacourse.shopping.utils.RemoteHost
import woowacourse.shopping.utils.ServerConfiguration

class ServerSelectionPresenter(
    val view: ServerSelectionContract.View
) : ServerSelectionContract.Presenter {
    override fun selectServer(serverId: Int) {
        ServerConfiguration.host = RemoteHost.values()[serverId]
        view.showProductListView()
    }
}
