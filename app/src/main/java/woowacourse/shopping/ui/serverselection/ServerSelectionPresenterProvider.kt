package woowacourse.shopping.ui.serverselection

object ServerSelectionPresenterProvider {
    fun create(view: ServerSelectionContract.View): ServerSelectionContract.Presenter {
        return ServerSelectionPresenter(view)
    }
}
