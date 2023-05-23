package woowacourse.shopping.ui.serversetting

import woowacourse.shopping.model.Server
import woowacourse.shopping.ui.serversetting.ServerSettingContract.Presenter
import woowacourse.shopping.ui.serversetting.ServerSettingContract.View

class ServerSettingPresenter(view: View) : Presenter(view) {
    override fun selectServer(server: Server) {
        view.navigateToShopping(server.url)
    }

}
