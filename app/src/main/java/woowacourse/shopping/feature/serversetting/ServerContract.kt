package woowacourse.shopping.feature.serversetting

interface ServerContract {
    interface View {
        fun showMainScreen(serverName: String)
    }

    interface Presenter {
        fun selectDeeTooServer()
        fun selectEmilServer()
        fun selectRoiseServer()
    }
}
