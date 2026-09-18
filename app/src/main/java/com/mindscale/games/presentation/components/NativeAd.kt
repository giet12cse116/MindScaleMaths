package com.mindscale.games.presentation.components

import android.content.Context
import android.graphics.Typeface
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.mindscale.games.BuildConfig

@Composable
fun NativeAd(
    modifier: Modifier = Modifier,
    adUnitId: String? = null
) {
    val effectiveAdUnitId = adUnitId ?: if (BuildConfig.DEBUG) {
        "ca-app-pub-3940256099942544/2247696110"
    } else {
        "ca-app-pub-5520583411219682/4023547003"
    }

    var loadedNativeAd by remember { mutableStateOf<NativeAd?>(null) }
    var adFailedToLoad by remember { mutableStateOf(false) }

    val context = LocalContext.current

    DisposableEffect(effectiveAdUnitId) {
        val adLoader = AdLoader.Builder(context, effectiveAdUnitId)
            .forNativeAd { ad ->
                loadedNativeAd?.destroy()
                loadedNativeAd = ad
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    adFailedToLoad = true
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder().build()
            )
            .build()

        adLoader.loadAd(AdRequest.Builder().build())

        onDispose {
            loadedNativeAd?.destroy()
        }
    }

    if (adFailedToLoad) {
        BannerAd(modifier = modifier, adUnitId = null)
    } else {
        AndroidView(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            factory = { ctx ->
                createNativeAdView(ctx)
            },
            update = { view ->
                loadedNativeAd?.let { ad ->
                    populateNativeAdView(ad, view)
                }
            }
        )
    }
}

private fun createNativeAdView(context: Context): NativeAdView {
    val nativeAdView = NativeAdView(context)
    val rootLayout = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(dpToPx(context, 14), dpToPx(context, 14), dpToPx(context, 14), dpToPx(context, 14))
        setBackgroundColor(0xFF121826.toInt()) // SurfaceCard background
    }

    // Top Header: Ad Badge Tag
    val badgeTextView = TextView(context).apply {
        text = "SPONSORED AD"
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 9f)
        setTextColor(0xFF8A93A6.toInt()) // TextSecondary
        typeface = Typeface.MONOSPACE
        setBackgroundColor(0xFF232B3D.toInt()) // SurfaceCardBorder
        setPadding(dpToPx(context, 6), dpToPx(context, 2), dpToPx(context, 6), dpToPx(context, 2))
    }
    val badgeRow = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        addView(badgeTextView)
    }
    rootLayout.addView(badgeRow)

    // Main Content Row: Icon + (Headline & Body)
    val contentRow = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        setPadding(0, dpToPx(context, 10), 0, dpToPx(context, 10))
    }

    val iconView = ImageView(context).apply {
        layoutParams = LinearLayout.LayoutParams(dpToPx(context, 48), dpToPx(context, 48)).apply {
            marginEnd = dpToPx(context, 12)
        }
    }
    nativeAdView.iconView = iconView
    contentRow.addView(iconView)

    val textColumn = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
    }

    val headlineView = TextView(context).apply {
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
        setTextColor(0xFFF5F7FA.toInt()) // TextPrimary
        typeface = Typeface.DEFAULT_BOLD
        maxLines = 1
        ellipsize = TextUtils.TruncateAt.END
    }
    nativeAdView.headlineView = headlineView
    textColumn.addView(headlineView)

    val bodyView = TextView(context).apply {
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
        setTextColor(0xFF8A93A6.toInt()) // TextSecondary
        maxLines = 2
        ellipsize = TextUtils.TruncateAt.END
    }
    nativeAdView.bodyView = bodyView
    textColumn.addView(bodyView)

    contentRow.addView(textColumn)
    rootLayout.addView(contentRow)

    // CTA Button
    val ctaButton = Button(context).apply {
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            dpToPx(context, 46)
        ).apply {
            topMargin = dpToPx(context, 4)
        }
        setBackgroundColor(0xFF2EE6A8.toInt()) // AccentMint
        setTextColor(0xFF0B0F1A.toInt()) // BackgroundDeep
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        typeface = Typeface.DEFAULT_BOLD
    }
    nativeAdView.callToActionView = ctaButton
    rootLayout.addView(ctaButton)

    nativeAdView.addView(rootLayout)
    return nativeAdView
}

private fun populateNativeAdView(nativeAd: NativeAd, nativeAdView: NativeAdView) {
    (nativeAdView.headlineView as? TextView)?.text = nativeAd.headline

    val body = nativeAd.body
    if (body.isNullOrEmpty()) {
        nativeAdView.bodyView?.visibility = View.GONE
    } else {
        nativeAdView.bodyView?.visibility = View.VISIBLE
        (nativeAdView.bodyView as? TextView)?.text = body
    }

    val icon = nativeAd.icon
    if (icon?.drawable == null) {
        nativeAdView.iconView?.visibility = View.GONE
    } else {
        nativeAdView.iconView?.visibility = View.VISIBLE
        (nativeAdView.iconView as? ImageView)?.setImageDrawable(icon.drawable)
    }

    val cta = nativeAd.callToAction
    if (cta.isNullOrEmpty()) {
        nativeAdView.callToActionView?.visibility = View.GONE
    } else {
        nativeAdView.callToActionView?.visibility = View.VISIBLE
        (nativeAdView.callToActionView as? Button)?.text = cta
    }

    nativeAdView.setNativeAd(nativeAd)
}

private fun dpToPx(context: Context, dp: Int): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        context.resources.displayMetrics
    ).toInt()
}
