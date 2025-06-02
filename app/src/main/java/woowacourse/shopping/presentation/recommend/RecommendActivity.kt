package woowacourse.shopping.presentation.recommend

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityRecommendBinding
import woowacourse.shopping.presentation.Extra
import woowacourse.shopping.presentation.cart.CartCounterClickListener
import woowacourse.shopping.presentation.model.CartItemUiModel

class RecommendActivity :
    AppCompatActivity(),
    RecommendItemClickListener,
    CartCounterClickListener {
    private lateinit var binding: ActivityRecommendBinding
    private lateinit var viewModel: RecommendViewModel
    private val recommendAdapter: RecommendAdapter by lazy {
        RecommendAdapter(this, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setupBinding()
        setupViewModel()
        initInsets()
        setupToolbar()
        initAdapter()
        observeViewModel()
    }

    private fun setupBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_recommend)
        binding.lifecycleOwner = this
    }

    private fun setupViewModel() {
        val price = intent.getIntExtra(Extra.KEY_SELECT_PRICE, 0)
        val count = intent.getIntExtra(Extra.KEY_SELECT_COUNT, 0)
        val defaultArgs =
            bundleOf(
                Extra.KEY_SELECT_PRICE to price,
                Extra.KEY_SELECT_COUNT to count,
            )
        val creationExtras =
            MutableCreationExtras(defaultViewModelCreationExtras).apply {
                this[VIEW_MODEL_DEFAULT_ARGS_KEY] = defaultArgs
            }
        val factory = RecommendViewModelFactory()
        viewModel =
            ViewModelProvider(
                viewModelStore,
                factory,
                creationExtras,
            )[RecommendViewModel::class.java]
        binding.vm = viewModel
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
        binding.rvRecommend.apply {
            adapter = recommendAdapter
            itemAnimator = null
        }
    }

    private fun observeViewModel() {
        viewModel.recommendProducts.observe(this) {
            recommendAdapter.submitList(it)
        }

        viewModel.toastMessage.observe(this) { resId ->
            showToast(resId)
        }
    }

    private fun showToast(
        @StringRes messageResId: Int,
    ) {
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
    }

    override fun onClickAddToCart(cartItem: CartItemUiModel) {
        viewModel.addToCart(cartItem)
    }

    override fun onClickPlus(id: Long) {
        viewModel.increaseQuantity(id)
    }

    override fun onClickMinus(id: Long) {
        viewModel.decreaseQuantity(id)
    }

    companion object {
        fun newIntent(
            context: Context,
            selectedPrice: Int,
            selectedCount: Int,
        ): Intent =
            Intent(context, RecommendActivity::class.java).apply {
                putExtra(Extra.KEY_SELECT_PRICE, selectedPrice)
                putExtra(Extra.KEY_SELECT_COUNT, selectedCount)
            }

        private val VIEW_MODEL_DEFAULT_ARGS_KEY = object : CreationExtras.Key<Bundle> {}
    }
}
