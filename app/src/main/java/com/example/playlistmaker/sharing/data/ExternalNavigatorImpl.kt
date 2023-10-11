package com.example.playlistmaker.sharing.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import com.example.playlistmaker.sharing.domain.models.EmailData

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator {

    override fun shareLink(shareAppLink: String) {
        Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareAppLink)
            type = "text/plain"
            context.startActivity(Intent.createChooser(this, null))
        }
    }

    override fun openLink(termsLink: String) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(termsLink)))
    }

    override fun openEmail(supportEmailData: EmailData) {
        Intent().apply {
            action = Intent.ACTION_SENDTO
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(supportEmailData.email))
            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.theme_support_message))
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.support_message))
            context.startActivity(this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }
}