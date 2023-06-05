package woowacourse.shopping.ui.serversetting

import woowacourse.shopping.model.Server
import woowacourse.shopping.ui.serversetting.ServerSettingContract.Presenter
import woowacourse.shopping.ui.serversetting.ServerSettingContract.View
import woowacourse.shopping.util.preference.BasePreference

class ServerSettingPresenter(
    private val view: View,
    private val shoppingPreference: BasePreference,
) : Presenter {

    override fun selectServer(server: Server) {
        shoppingPreference.setToken("dG1kZ2gxNTkyQG5hdmVyLmNvbToxMjM0")
        shoppingPreference.setBaseUrl(server.url)
        view.navigateToShopping(server.url)
    }
}

