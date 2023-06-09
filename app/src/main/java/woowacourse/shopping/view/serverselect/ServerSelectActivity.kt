package woowacourse.shopping.view.serverselect

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.databinding.ActivityServerSelectBinding
import woowacourse.shopping.server.Server
import woowacourse.shopping.server.retrofit.RetrofitClient
import woowacourse.shopping.view.shoppingmain.ShoppingMainActivity

class ServerSelectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityServerSelectBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityServerSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setServerButton()
    }

    private fun setServerButton() {
        binding.btnJerryServer.setOnClickListener { startShopping(Server.JERRY.name) }
        binding.btnGitjjangServer.setOnClickListener { startShopping(Server.GITJJANG.name) }
        binding.btnHoiServer.setOnClickListener { startShopping(Server.HOI.name) }
    }

    private fun startShopping(serverUrl: String) {
        RetrofitClient.initBaseUrl(serverUrl)
        val intent = ShoppingMainActivity.intent(this)
        startActivity(intent)
        finish()
    }
}
