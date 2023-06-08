package woowacourse.shopping.view.serverselect

interface ServerSelectContract {
    interface View {
        fun executeActivity()
    }
    interface Presenter {
        fun selectServer(server: Server)
    }
}
