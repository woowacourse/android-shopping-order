package woowacourse.shopping.ui.serverselection

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import woowacourse.shopping.R
import woowacourse.shopping.ui.products.ProductListActivity
import woowacourse.shopping.utils.UserData

class ServerSelectionActivity : AppCompatActivity(), ServerSelectionContract.View {

    private lateinit var presenter: ServerSelectionPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server_selection)

        saveUserInfo("YUBhLmNvbToxMjM0")
        initServerButton()
        initPresenter()
    }

    override fun showProductListView() {
        ProductListActivity.startActivity(this)
    }

    private fun saveUserInfo(credential: String) {
        UserData.credential = credential
        val preferences = getSharedPreferences("shopping", MODE_PRIVATE)
        preferences.edit().putString("user", credential)
            .apply()
    }

    private fun initServerButton() {
        findViewById<LinearLayout>(R.id.layout_server_selection).children.filterIsInstance<Button>()
            .forEachIndexed { index, button ->
                button.setOnClickListener {
                    presenter.selectServer(index)
                }
            }
    }

    private fun initPresenter() {
        presenter = ServerSelectionPresenter(this)
    }
}
