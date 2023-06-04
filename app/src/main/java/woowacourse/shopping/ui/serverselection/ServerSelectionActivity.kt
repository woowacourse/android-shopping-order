package woowacourse.shopping.ui.serverselection

import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import woowacourse.shopping.R
import woowacourse.shopping.ui.products.ProductListActivity
import woowacourse.shopping.utils.UserData

class ServerSelectionActivity : AppCompatActivity(), ServerSelectionContract.View {

    private val presenter: ServerSelectionPresenter by lazy {
        ServerSelectionPresenter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server_selection)

        initUserInfo()
        initServerButton()
    }

    override fun showProductListView() {
        ProductListActivity.startActivity(this)
    }

    private fun initUserInfo() {
        val preferences = getSharedPreferences("shopping", MODE_PRIVATE)
        var email = preferences.getString("EMAIL", null)
        val password: String?
        val grade: String?
        if (email == null) {
            email = "a@a.com"
            password = "1234"
            grade = "GOLD"
            preferences.edit()
                .putString("EMAIL", email)
                .putString("PASSWORD", password)
                .putString("GRADE", grade)
                .apply()
        } else {
            password = preferences.getString("PASSWORD", null)
                ?: throw IllegalStateException("사용자의 정보를 초기화 할 때 유저의 정보 중 이메일만 저장하고 비밀번호는 저장하지 않았음. 사용자의 정보를 초기화 하는 코드를 다시 보세요.")
            grade = preferences.getString("GRADE", null)
                ?: throw IllegalStateException("사용자의 정보를 초기화 할 때 유저의 정보 중 이메일만 저장하고 등급은 저장하지 않았음. 사용자의 정보를 초기화 하는 코드를 다시 보세요.")
        }
        UserData.credential = Base64.encodeToString(
            "$email:$password".toByteArray(),
            Base64.NO_WRAP
        )
        UserData.email = email
        UserData.grade = grade
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
