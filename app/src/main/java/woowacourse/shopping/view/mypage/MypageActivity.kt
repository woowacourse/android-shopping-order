package woowacourse.shopping.view.mypage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.data.repository.MypageRemoteRepository
import woowacourse.shopping.data.repository.ServerPreferencesRepository
import woowacourse.shopping.databinding.ActivityMypageBinding

class MypageActivity : AppCompatActivity() {
    private val binding: ActivityMypageBinding by lazy {
        ActivityMypageBinding.inflate(
            layoutInflater,
        )
    }
    private val presenter: MypageContract.Presenter by lazy {
        MypagePresenter(
            MypageRemoteRepository(ServerPreferencesRepository(this).getServerUrl()),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setUpBinding()
        setUpActionBar()
        presenter.fetchCash()
    }

    private fun setUpBinding() {
        binding.presenter = presenter
        binding.lifecycleOwner = this
        binding.btnSubmit.setOnClickListener {
            presenter.chargeCash(Integer.parseInt(binding.editCash.text.toString()))
        }
    }

    private fun setUpActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MypageActivity::class.java)
        }
    }
}
