package woowacourse.shopping.ui.serverSetting

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
            startMain(SERVER_IO)
        }
        buttonLeah.setOnClickListener {
            startMain(SERVER_JITO)
        }
    }

    private fun startMain(server: String) {
        startActivity(ShoppingActivity.getIntent(this, server))
        finish()
    }

    companion object {
        const val SERVER_IO = "http://43.200.169.154:8080"
        const val SERVER_JITO = "http://13.125.207.155:8080"
    }
}
