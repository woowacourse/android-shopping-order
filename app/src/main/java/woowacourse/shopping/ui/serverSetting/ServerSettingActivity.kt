package woowacourse.shopping.ui.serverSetting

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.ui.shopping.ShoppingActivity
import woowacourse.shopping.utils.RetrofitUtil

class ServerSettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_server_setting)
        setupView()
    }

    private fun setupView() {
        val buttonIo = findViewById<Button>(R.id.btn_io_server)
        val buttonJito = findViewById<Button>(R.id.btn_jito_server)
        buttonIo.setOnClickListener {
            RetrofitUtil.getInstance(SERVER_IO, "Basic YUBhLmNvbToxMjM0")
            startMain()
        }
        buttonJito.setOnClickListener {
            RetrofitUtil.getInstance(SERVER_JITO, "Basic YUBhLmNvbToxMjM0")
            startMain()
        }
    }

    private fun startMain() {
        startActivity(
            ShoppingActivity.getIntent(this).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
        )
        finish()
    }

    companion object {
        const val SERVER_IO = "http://43.200.169.154:8080"
        const val SERVER_JITO = "http://13.125.207.155:8080"
    }
}
