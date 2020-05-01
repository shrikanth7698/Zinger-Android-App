package com.food.ordering.zinger.ui.contributors

import android.animation.LayoutTransition
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.food.ordering.zinger.R
import com.food.ordering.zinger.data.local.PreferencesHelper
import com.food.ordering.zinger.data.model.ContributorModel
import com.food.ordering.zinger.databinding.ActivityContributorDetailBinding
import com.squareup.picasso.Picasso
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class ContributorDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContributorDetailBinding
    private val preferencesHelper: PreferencesHelper by inject()
    private val viewModel: ContributorViewModel by viewModel()
    private var contributorId = 0
    private lateinit var contributor: ContributorModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
        initView()
        setListener()
        updateUI()
    }

    private fun getArgs() {
        contributorId = intent.getIntExtra("contributor_id", 0)
        contributor = viewModel.getContributor(contributorId)
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contributor_detail)
        binding.layoutContent.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
    }

    private fun setListener() {
        binding.imageClose.setOnClickListener {
            onBackPressed()
        }
        binding.cardGithub.setOnClickListener {
            openLink(contributor.github)
        }
        binding.cardLinkedin.setOnClickListener {
            openLink(contributor.linkedIn)
        }
        binding.cardEmail.setOnClickListener {
            val intent =  Intent(Intent.ACTION_SEND)
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(contributor.mail))
            intent.putExtra(Intent.EXTRA_SUBJECT,"Regarding Zinger App")
            intent.putExtra(Intent.EXTRA_TEXT,"Mail regarding Zinger App")
            intent.type = "message/rfc822"
            val chooserIntent = Intent.createChooser(intent,"Send Email")
            startActivity(chooserIntent)
        }
        binding.cardWebsite.setOnClickListener {
            openLink(contributor.website)
        }
    }

    private fun updateUI() {
        Picasso.get().load(contributor.image).into(binding.imageUser)
        binding.textName.text = contributor.name
        binding.textRole.text = contributor.role
        binding.textGithub.text = contributor.github
        binding.textLinkedin.text = contributor.linkedIn
        binding.textEmail.text = contributor.mail
        binding.textWebsite.text = contributor.website
    }

    private fun openLink(link: String) {
        val defaultBrowser = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER)
        defaultBrowser.data = Uri.parse(link)
        startActivity(defaultBrowser)
    }


}
