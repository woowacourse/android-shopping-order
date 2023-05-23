package woowacourse.shopping.ui.serversetting

import woowacourse.shopping.model.Server

interface ServerSettingContract {
    interface View {
        fun navigateToShopping(serverUrl: String)
    }

    abstract class Presenter(protected val view: View) {
        abstract fun selectServer(server: Server)
    }
}
