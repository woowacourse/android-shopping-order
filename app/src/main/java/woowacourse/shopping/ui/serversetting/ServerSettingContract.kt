package woowacourse.shopping.ui.serversetting

import woowacourse.shopping.model.Server

interface ServerSettingContract {
    interface View {
        fun navigateToShopping(serverUrl: String)
    }

    interface Presenter {
        fun selectServer(server: Server)
    }
}
