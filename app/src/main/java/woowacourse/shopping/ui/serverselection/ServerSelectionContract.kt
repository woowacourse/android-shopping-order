package woowacourse.shopping.ui.serverselection

interface ServerSelectionContract {
    interface Presenter {
        fun selectServer(serverId: Int)
    }

    interface View {
        fun showProductListView()
    }
}
