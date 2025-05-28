package woowacourse.shopping.presentation.recommend

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityRecommendBinding

class RecommendActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecommendBinding
    private val viewModel: RecommendViewModel by viewModels { RecommendViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_recommend)
        binding.lifecycleOwner = this

        initInsets()
        setupToolbar()

//        binding.vm = viewModel
//        binding.itemClickListener = this

//        initListeners()
//        observeViewModel()
        initAdapter()
        viewModel.fetchData()
    }

    private fun initInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.clRecommend) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.tbRecommend)
        binding.tbRecommend.apply {
            setNavigationIcon(R.drawable.ic_back)
            setNavigationOnClickListener {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    private fun initAdapter() {
//        adapter = OrderAdapter()
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, RecommendActivity::class.java)
    }
}
