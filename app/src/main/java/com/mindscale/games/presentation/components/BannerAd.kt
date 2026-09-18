package com.mindscale.games.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

import com.mindscale.games.BuildConfig

@Composable
fun BannerAd(
    modifier: Modifier = Modifier,
    adUnitId: String? = null
) {
    val effectiveAdUnitId = adUnitId ?: if (BuildConfig.DEBUG) {
        "ca-app-pub-3940256099942544/6300978111"
    } else {
        "ca-app-pub-5520583411219682/9546055166"
    }

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                this.adUnitId = effectiveAdUnitId
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}
