package com.solodroidbal.ads.sdk.format;

import static com.solodroidbal.ads.sdk.util.Constant.ADMOB;
import static com.solodroidbal.ads.sdk.util.Constant.AD_STATUS_ON;
import static com.solodroidbal.ads.sdk.util.Constant.APPLOVIN;
import static com.solodroidbal.ads.sdk.util.Constant.APPLOVIN_MAX;
import static com.solodroidbal.ads.sdk.util.Constant.NONE;


import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.applovin.mediation.nativeAds.MaxNativeAdViewBinder;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.MediaView;
import com.solodroidbal.ads.sdk.R;
import com.solodroidbal.ads.sdk.util.Constant;
import com.solodroidbal.ads.sdk.util.NativeTemplateStyle;
import com.solodroidbal.ads.sdk.util.TemplateView;
import com.solodroidbal.ads.sdk.util.Tools;


import java.util.ArrayList;

public class NativeAdViewPager {

    public static class Builder {

        private static final String TAG = "AdNetwork";
        private final Activity activity;

        View view;
        MediaView mediaView;
        TemplateView admob_native_ad;
        LinearLayout admob_native_background;
        View startapp_native_ad;
        ImageView startapp_native_image;
        ImageView startapp_native_icon;
        TextView startapp_native_title;
        TextView startapp_native_description;
        Button startapp_native_button;
        LinearLayout startapp_native_background;

        FrameLayout applovin_native_ad;
        MaxNativeAdLoader nativeAdLoader;
        MaxAd nativeAd;

        ProgressBar progress_bar_ad;

        private String adStatus = "";
        private String adNetwork = "";
        private String backupAdNetwork = "";
        private String adMobNativeId = "";
        private String appLovinNativeId = "";
        private int placementStatus = 1;
        private boolean darkTheme = false;
        private boolean legacyGDPR = false;

        public Builder(Activity activity, View view) {
            this.activity = activity;
            this.view = view;
        }

        public Builder build() {
            loadNativeAd();
            return this;
        }

        public Builder setAdStatus(String adStatus) {
            this.adStatus = adStatus;
            return this;
        }

        public Builder setAdNetwork(String adNetwork) {
            this.adNetwork = adNetwork;
            return this;
        }

        public Builder setBackupAdNetwork(String backupAdNetwork) {
            this.backupAdNetwork = backupAdNetwork;
            return this;
        }

        public Builder setAdMobNativeId(String adMobNativeId) {
            this.adMobNativeId = adMobNativeId;
            return this;
        }

        public Builder setAppLovinNativeId(String appLovinNativeId) {
            this.appLovinNativeId = appLovinNativeId;
            return this;
        }

        public Builder setPlacementStatus(int placementStatus) {
            this.placementStatus = placementStatus;
            return this;
        }

        public Builder setDarkTheme(boolean darkTheme) {
            this.darkTheme = darkTheme;
            return this;
        }

        public Builder setLegacyGDPR(boolean legacyGDPR) {
            this.legacyGDPR = legacyGDPR;
            return this;
        }

        public void loadNativeAd() {

            if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {

                admob_native_ad = view.findViewById(R.id.admob_native_ad_container);
                mediaView = view.findViewById(R.id.media_view);
                admob_native_background = view.findViewById(R.id.background);
                startapp_native_ad = view.findViewById(R.id.startapp_native_ad_container);
                startapp_native_image = view.findViewById(R.id.startapp_native_image);
                startapp_native_icon = activity.findViewById(R.id.startapp_native_icon);
                startapp_native_title = view.findViewById(R.id.startapp_native_title);
                startapp_native_description = view.findViewById(R.id.startapp_native_description);
                startapp_native_button = view.findViewById(R.id.startapp_native_button);
                startapp_native_button.setOnClickListener(v1 -> startapp_native_ad.performClick());
                startapp_native_background = view.findViewById(R.id.startapp_native_background);
                applovin_native_ad = view.findViewById(R.id.applovin_native_ad_container);
                progress_bar_ad = view.findViewById(R.id.progress_bar_ad);
                progress_bar_ad.setVisibility(View.VISIBLE);

                switch (adNetwork) {
                    case ADMOB:
                        if (admob_native_ad.getVisibility() != View.VISIBLE) {
                            AdLoader adLoader = new AdLoader.Builder(activity, adMobNativeId)
                                    .forNativeAd(NativeAd -> {
                                        if (darkTheme) {
                                            ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(activity, R.color.colorBackgroundDark));
                                            NativeTemplateStyle styles = new NativeTemplateStyle.Builder().withMainBackgroundColor(colorDrawable).build();
                                            admob_native_ad.setStyles(styles);
                                            admob_native_background.setBackgroundResource(R.color.colorBackgroundDark);
                                        } else {
                                            ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(activity, R.color.colorBackgroundLight));
                                            NativeTemplateStyle styles = new NativeTemplateStyle.Builder().withMainBackgroundColor(colorDrawable).build();
                                            admob_native_ad.setStyles(styles);
                                            admob_native_background.setBackgroundResource(R.color.colorBackgroundLight);
                                        }
                                        mediaView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
                                        admob_native_ad.setNativeAd(NativeAd);
                                        admob_native_ad.setVisibility(View.VISIBLE);
                                        progress_bar_ad.setVisibility(View.GONE);
                                    })
                                    .withAdListener(new AdListener() {
                                        @Override
                                        public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                                            loadBackupNativeAd();
                                        }
                                    })
                                    .build();
                            adLoader.loadAd(Tools.getAdRequest(activity, legacyGDPR));
                        } else {
                            Log.d(TAG, "AdMob Native Ad has been loaded");
                            progress_bar_ad.setVisibility(View.GONE);
                        }
                        break;


                    case APPLOVIN_MAX:
                    case APPLOVIN:
                        if (applovin_native_ad.getVisibility() != View.VISIBLE) {
                            nativeAdLoader = new MaxNativeAdLoader(appLovinNativeId, activity);
                            nativeAdLoader.setNativeAdListener(new MaxNativeAdListener() {
                                @Override
                                public void onNativeAdLoaded(final MaxNativeAdView nativeAdView, final MaxAd ad) {
                                    // Clean up any pre-existing native ad to prevent memory leaks.
                                    if (nativeAd != null) {
                                        nativeAdLoader.destroy(nativeAd);
                                    }

                                    // Save ad for cleanup.
                                    nativeAd = ad;

                                    // Add ad view to view.
                                    applovin_native_ad.removeAllViews();
                                    applovin_native_ad.addView(nativeAdView);
                                    applovin_native_ad.setVisibility(View.VISIBLE);
                                    progress_bar_ad.setVisibility(View.GONE);
                                }

                                @Override
                                public void onNativeAdLoadFailed(final String adUnitId, final MaxError error) {
                                    // We recommend retrying with exponentially higher delays up to a maximum delay
                                    loadBackupNativeAd();
                                }

                                @Override
                                public void onNativeAdClicked(final MaxAd ad) {
                                    // Optional click callback
                                }
                            });
                            nativeAdLoader.loadAd(createNativeAdView());
                        } else {
                            Log.d(TAG, "AppLovin Native Ad has been loaded");
                            progress_bar_ad.setVisibility(View.GONE);
                        }
                        break;


                }

            }

        }

        public void loadBackupNativeAd() {

            if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {

                admob_native_ad = view.findViewById(R.id.admob_native_ad_container);
                mediaView = view.findViewById(R.id.media_view);
                admob_native_background = view.findViewById(R.id.background);
                startapp_native_ad = view.findViewById(R.id.startapp_native_ad_container);
                startapp_native_image = view.findViewById(R.id.startapp_native_image);
                startapp_native_icon = activity.findViewById(R.id.startapp_native_icon);
                startapp_native_title = view.findViewById(R.id.startapp_native_title);
                startapp_native_description = view.findViewById(R.id.startapp_native_description);
                startapp_native_button = view.findViewById(R.id.startapp_native_button);
                startapp_native_button.setOnClickListener(v1 -> startapp_native_ad.performClick());
                startapp_native_background = view.findViewById(R.id.startapp_native_background);
                progress_bar_ad = view.findViewById(R.id.progress_bar_ad);
                progress_bar_ad.setVisibility(View.VISIBLE);

                switch (backupAdNetwork) {
                    case ADMOB:
                        if (admob_native_ad.getVisibility() != View.VISIBLE) {
                            AdLoader adLoader = new AdLoader.Builder(activity, adMobNativeId)
                                    .forNativeAd(NativeAd -> {
                                        if (darkTheme) {
                                            ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(activity, R.color.colorBackgroundDark));
                                            NativeTemplateStyle styles = new NativeTemplateStyle.Builder().withMainBackgroundColor(colorDrawable).build();
                                            admob_native_ad.setStyles(styles);
                                            admob_native_background.setBackgroundResource(R.color.colorBackgroundDark);
                                        } else {
                                            ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(activity, R.color.colorBackgroundLight));
                                            NativeTemplateStyle styles = new NativeTemplateStyle.Builder().withMainBackgroundColor(colorDrawable).build();
                                            admob_native_ad.setStyles(styles);
                                            admob_native_background.setBackgroundResource(R.color.colorBackgroundLight);
                                        }
                                        mediaView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
                                        admob_native_ad.setNativeAd(NativeAd);
                                        admob_native_ad.setVisibility(View.VISIBLE);
                                        progress_bar_ad.setVisibility(View.GONE);
                                    })
                                    .withAdListener(new AdListener() {
                                        @Override
                                        public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                                            admob_native_ad.setVisibility(View.GONE);
                                            progress_bar_ad.setVisibility(View.GONE);
                                        }
                                    })
                                    .build();
                            adLoader.loadAd(Tools.getAdRequest(activity, legacyGDPR));
                        } else {
                            Log.d(TAG, "AdMob Native Ad has been loaded");
                            progress_bar_ad.setVisibility(View.GONE);
                        }
                        break;

                    case APPLOVIN_MAX:
                    case APPLOVIN:
                        if (applovin_native_ad.getVisibility() != View.VISIBLE) {
                            nativeAdLoader = new MaxNativeAdLoader(appLovinNativeId, activity);
                            nativeAdLoader.setNativeAdListener(new MaxNativeAdListener() {
                                @Override
                                public void onNativeAdLoaded(final MaxNativeAdView nativeAdView, final MaxAd ad) {
                                    // Clean up any pre-existing native ad to prevent memory leaks.
                                    if (nativeAd != null) {
                                        nativeAdLoader.destroy(nativeAd);
                                    }

                                    // Save ad for cleanup.
                                    nativeAd = ad;

                                    // Add ad view to view.
                                    applovin_native_ad.removeAllViews();
                                    applovin_native_ad.addView(nativeAdView);
                                    applovin_native_ad.setVisibility(View.VISIBLE);
                                    progress_bar_ad.setVisibility(View.GONE);
                                }

                                @Override
                                public void onNativeAdLoadFailed(final String adUnitId, final MaxError error) {
                                    // We recommend retrying with exponentially higher delays up to a maximum delay
                                }

                                @Override
                                public void onNativeAdClicked(final MaxAd ad) {
                                    // Optional click callback
                                }
                            });
                            nativeAdLoader.loadAd(createNativeAdView());
                        } else {
                            Log.d(TAG, "AppLovin Native Ad has been loaded");
                            progress_bar_ad.setVisibility(View.GONE);
                        }
                        break;



                    case NONE:
                        //do nothing
                        break;

                }

            }

        }

        public MaxNativeAdView createNativeAdView() {
            MaxNativeAdViewBinder binder = new MaxNativeAdViewBinder.Builder(R.layout.gnt_applovin_large_template_view)
                    .setTitleTextViewId(R.id.title_text_view)
                    .setBodyTextViewId(R.id.body_text_view)
                    .setAdvertiserTextViewId(R.id.advertiser_textView)
                    .setIconImageViewId(R.id.icon_image_view)
                    .setMediaContentViewGroupId(R.id.media_view_container)
                    .setOptionsContentViewGroupId(R.id.ad_options_view)
                    .setCallToActionButtonId(R.id.cta_button)
                    .build();
            return new MaxNativeAdView(binder, activity);
        }

    }

}
