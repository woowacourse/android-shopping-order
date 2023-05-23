package woowacourse.shopping.ui.serversetting

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.ui.shopping.ShoppingActivity

class ServerSettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_server_setting)
        setupView()
    }

    private fun setupView() {
        val buttonJames = findViewById<Button>(R.id.btn_james_server)
        val buttonLeah = findViewById<Button>(R.id.btn_leah_server)
        buttonJames.setOnClickListener {
            startMain(SERVER_JAMES)
        }
        buttonLeah.setOnClickListener {
            startMain(SERVER_LEAH)
        }
    }

    private fun startMain(serverUrl: String) {
        startActivity(ShoppingActivity.getIntent(this, serverUrl))
        finish()
    }

    companion object {
        private const val SERVER_JAMES = "james"
        private const val SERVER_LEAH = "leah"
    }
}
