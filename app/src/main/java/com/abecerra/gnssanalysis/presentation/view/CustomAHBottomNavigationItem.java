package com.abecerra.gnssanalysis.presentation.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.appcompat.content.res.AppCompatResources;

public class CustomAHBottomNavigationItem {


    private String title = "";
    private Drawable drawable;
    private int color = Color.GRAY;
    private boolean notified = false;

    private
    @StringRes
    int titleRes = 0;
    private
    @DrawableRes
    int drawableRes = 0;
    private
    @ColorRes
    int colorRes = 0;

    /**
     * Constructor
     *
     * @param title    Title
     * @param resource Drawable resource
     */
    public CustomAHBottomNavigationItem(String title, @DrawableRes int resource) {
        this.title = title;
        this.drawableRes = resource;
    }

    /**
     * @param title    Title
     * @param resource Drawable resource
     * @param color    Background color
     */
    @Deprecated
    public CustomAHBottomNavigationItem(String title, @DrawableRes int resource, @ColorRes int color) {
        this.title = title;
        this.drawableRes = resource;
        this.color = color;
    }

    /**
     * Constructor
     *
     * @param titleRes    String resource
     * @param drawableRes Drawable resource
     * @param colorRes    Color resource
     */
    public CustomAHBottomNavigationItem(@StringRes int titleRes, @DrawableRes int drawableRes, @ColorRes int colorRes) {
        this.titleRes = titleRes;
        this.drawableRes = drawableRes;
        this.colorRes = colorRes;
    }

    /**
     * Constructor
     *
     * @param title    String
     * @param drawable Drawable
     */
    public CustomAHBottomNavigationItem(String title, Drawable drawable) {
        this.title = title;
        this.drawable = drawable;
    }

    /**
     * Constructor
     *
     * @param title    String
     * @param drawable Drawable
     * @param color    Color
     */
    public CustomAHBottomNavigationItem(String title, Drawable drawable, @ColorInt int color) {
        this.title = title;
        this.drawable = drawable;
        this.color = color;
    }

    public String getTitle(Context context) {
        if (titleRes != 0) {
            return context.getString(titleRes);
        }
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.titleRes = 0;
    }

    public void setTitle(@StringRes int titleRes) {
        this.titleRes = titleRes;
        this.title = "";
    }

    public int getColor(Context context) {
        if (colorRes != 0) {
            return ContextCompat.getColor(context, colorRes);
        }
        return color;
    }

    public void setColor(@ColorInt int color) {
        this.color = color;
        this.colorRes = 0;
    }

    public void setColorRes(@ColorRes int colorRes) {
        this.colorRes = colorRes;
        this.color = 0;
    }

    public Drawable getDrawable(Context context) {
        if (drawableRes != 0) {
            try {
                return AppCompatResources.getDrawable(context, drawableRes);
            } catch (Resources.NotFoundException e) {
                return ContextCompat.getDrawable(context, drawableRes);
            }
        }
        return drawable;
    }

    public void setDrawable(@DrawableRes int drawableRes) {
        this.drawableRes = drawableRes;
        this.drawable = null;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
        this.drawableRes = 0;
    }

    public void setNotified(boolean notified){
        this.notified = notified;
    }

    public boolean isNotified(){
        return notified;
    }


}
