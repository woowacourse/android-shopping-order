package woowacourse.shopping.ui.serversetting

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.data.remote.RetrofitModule
import woowacourse.shopping.ui.shopping.ShoppingActivity
import woowacourse.shopping.util.setThrottleFirstOnClickListener

class ServerSettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_server_setting)
        setupView()
    }

    private fun setupView() {
        val buttonEndel = findViewById<Button>(R.id.btn_endel_server)
        val buttonHardy = findViewById<Button>(R.id.btn_hardy_server)
        val buttonOdo = findViewById<Button>(R.id.btn_odo_server)
        buttonEndel.setThrottleFirstOnClickListener {
            startMain(ENDEL_BASE_URL)
        }
        buttonHardy.setThrottleFirstOnClickListener {
            startMain(HARDY_BASE_URL)
        }
        buttonOdo.setThrottleFirstOnClickListener {
            startMain(ODO_BASE_URL)
        }
    }

    private fun startMain(baseUrl: String) {
        RetrofitModule.BASE_URL = baseUrl
        val intent = Intent(this, ShoppingActivity::class.java)
        startActivity(intent)
        finish()
    }

    companion object {
        private const val ENDEL_BASE_URL = "http://15.164.103.138:8080"
        private const val HARDY_BASE_URL = "http://15.164.234.30:8080"
        private const val ODO_BASE_URL = "http://3.39.9.184:8080"
    }
}
