package woowacourse.shopping.ui.serverselection

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import woowacourse.shopping.R
import woowacourse.shopping.ui.shopping.ShoppingActivity

class ServerSelectionActivity : AppCompatActivity(), ServerSelectionContract.View {

    private val presenter: ServerSelectionContract.Presenter by lazy {
        ServerSelectionPresenterProvider.create(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server_selection)

        initServerButton()
    }

    override fun showProductListView() {
        ShoppingActivity.startActivity(this)
    }

    private fun initServerButton() {
        findViewById<LinearLayout>(R.id.layout_server_selection).children.filterIsInstance<Button>()
            .forEachIndexed { index, button ->
                button.setOnClickListener {
                    presenter.selectServer(index)
                }
            }
    }
}
