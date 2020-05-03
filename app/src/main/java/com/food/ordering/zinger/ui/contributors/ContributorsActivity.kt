package com.food.ordering.zinger.ui.contributors

import android.animation.LayoutTransition
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.food.ordering.zinger.R
import com.food.ordering.zinger.data.local.PreferencesHelper
import com.food.ordering.zinger.databinding.ActivityContactUsBinding
import com.food.ordering.zinger.databinding.ActivityContributorsBinding
import com.food.ordering.zinger.databinding.ActivityPaymentBinding
import com.food.ordering.zinger.ui.placeorder.PlaceOrderActivity
import com.food.ordering.zinger.utils.AppConstants
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.squareup.picasso.Picasso
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class ContributorsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContributorsBinding
    private val preferencesHelper: PreferencesHelper by inject()
    private val viewModel: ContributorViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        setListener()
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contributors)
        binding.layoutContent.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        Picasso.get().load(viewModel.getContributor(0).image).into(binding.imageShrikanth)
        Picasso.get().load(viewModel.getContributor(1).image).into(binding.imageHarsha)
        Picasso.get().load(viewModel.getContributor(2).image).into(binding.imageLogesh)
    }

    private fun setListener(){
        binding.imageClose.setOnClickListener {
            onBackPressed()
        }
        binding.layoutShrikanth.setOnClickListener {
            val intent = Intent(applicationContext,ContributorDetailActivity::class.java)
            intent.putExtra("contributor_id",0)
            startActivity(intent)
        }
        binding.layoutHarsha.setOnClickListener {
            val intent = Intent(applicationContext,ContributorDetailActivity::class.java)
            intent.putExtra("contributor_id",1)
            startActivity(intent)
        }
        binding.layoutLogesh.setOnClickListener {
            val intent = Intent(applicationContext,ContributorDetailActivity::class.java)
            intent.putExtra("contributor_id",2)
            startActivity(intent)
        }
    }

}
