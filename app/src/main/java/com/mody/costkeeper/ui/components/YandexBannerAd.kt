package com.mody.costkeeper.ui.components

import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdRequest

@Composable
fun YandexBannerAd(
    adUnitId: String,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            BannerAdView(context).apply {
                setAdUnitId(adUnitId)
                setAdSize(BannerAdSize.stickySize(context, 320))
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}