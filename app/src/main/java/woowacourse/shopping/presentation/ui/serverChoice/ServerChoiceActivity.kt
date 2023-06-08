package woowacourse.shopping.presentation.ui.serverChoice

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.data.local.WoowaSharedPreference
import woowacourse.shopping.data.remote.ServicePool
import woowacourse.shopping.databinding.ActivityServerChoiceBinding
import woowacourse.shopping.presentation.ui.home.HomeActivity

class ServerChoiceActivity : AppCompatActivity(), ServerChoiceContract.View {
    private lateinit var binding: ActivityServerChoiceBinding
    private val presenter: ServerChoiceContract.Presenter by lazy {
        ServerChoicePresenter(WoowaSharedPreference(this), this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServerChoiceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.buttonSungha.setOnClickListener { selectSungha() }
        binding.buttonLogeon.setOnClickListener { selectLogeon() }
    }

    private fun selectSungha() {
        presenter.setServer(ServicePool.UrlPool.SUNGHA)
    }

    private fun selectLogeon() {
        presenter.setServer(ServicePool.UrlPool.LOGEON)
    }

    override fun setServer(url: ServicePool.UrlPool, token: String) {
        ServicePool.init(url, token)
        startActivity(Intent(this, HomeActivity::class.java))
    }
}
