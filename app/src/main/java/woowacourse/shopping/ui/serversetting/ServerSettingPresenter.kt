package woowacourse.shopping.ui.serversetting

import woowacourse.shopping.model.Server
import woowacourse.shopping.ui.serversetting.ServerSettingContract.Presenter
import woowacourse.shopping.ui.serversetting.ServerSettingContract.View
import woowacourse.shopping.util.preference.BasePreference

class ServerSettingPresenter(
    view: View,
    private val shoppingPreference: BasePreference,
) : Presenter(view) {

    override fun selectServer(server: Server) {
        shoppingPreference.setBaseUrl(server.url)
        view.navigateToShopping(server.url)
    }
}

