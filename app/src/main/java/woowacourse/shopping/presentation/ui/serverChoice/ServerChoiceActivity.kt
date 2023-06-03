package woowacourse.shopping.presentation.ui.serverChoice

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.data.remote.ServicePool
import woowacourse.shopping.databinding.ActivityServerChoiceBinding
import woowacourse.shopping.presentation.ui.home.HomeActivity

class ServerChoiceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityServerChoiceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServerChoiceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.buttonSungha.setOnClickListener { selectSungha() }
        binding.buttonLogeon.setOnClickListener { selectLogeon() }
    }

    private fun selectSungha() {
        ServicePool.init(ServicePool.UrlPool.SUNGHA)
        startActivity(Intent(this, HomeActivity::class.java))
    }

    private fun selectLogeon() {
        ServicePool.init(ServicePool.UrlPool.LOGEON)
        startActivity(Intent(this, HomeActivity::class.java))
    }
}
