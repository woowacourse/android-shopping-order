package woowacourse.shopping.view.serverselect

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.data.repository.ServerPreferencesRepository
import woowacourse.shopping.databinding.ActivityServerSelectBinding
import woowacourse.shopping.view.productlist.ProductListActivity

class ServerSelectActivity : AppCompatActivity(), ServerSelectContract.View {
    private val presenter by lazy { ServerSelectPresenter(this, ServerPreferencesRepository(this)) }
    private val binding by lazy { ActivityServerSelectBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.presenter = presenter
    }

    override fun executeActivity() {
        startActivity(ProductListActivity.newIntent(this))
    }
}
