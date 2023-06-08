package woowacourse.shopping.presentation.ui.serverChoice

import woowacourse.shopping.data.remote.ServicePool

interface ServerChoiceContract {
    interface View {
        fun setServer(url: ServicePool.UrlPool, token: String)
    }

    interface Presenter {
        fun setServer(url: ServicePool.UrlPool)
    }
}
