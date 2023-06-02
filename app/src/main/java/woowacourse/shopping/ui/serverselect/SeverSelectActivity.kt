package woowacourse.shopping.ui.serverselect

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.Storage
import woowacourse.shopping.databinding.ActivitySeverSelectBinding
import woowacourse.shopping.data.server.Server
import woowacourse.shopping.ui.shopping.ShoppingActivity

class SeverSelectActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySeverSelectBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        setContentView(binding.root)

        setupServerButton()
    }

    private fun initBinding() {
        binding = ActivitySeverSelectBinding.inflate(layoutInflater)
    }

    private fun setupServerButton() {
        binding.btnJerryServer.setOnClickListener { startShopping(Server.JERRY.name) }
        binding.btnGitjjangServer.setOnClickListener { startShopping(Server.GITJJANG.name) }
        binding.btnHoiServer.setOnClickListener { startShopping(Server.HOI.name) }
    }

    private fun startShopping(server: String) {
        Storage.server = server
        val intent = ShoppingActivity.createIntent(this)
        startActivity(intent)
        finish()
    }
}
