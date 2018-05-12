package com.phdlabs.sungwon.a8chat_android.structure.core;

import android.annotation.SuppressLint;
import android.app.ListFragment;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.phdlabs.sungwon.a8chat_android.R;
import com.phdlabs.sungwon.a8chat_android.utility.camera.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by SungWon on 9/11/2017.
 * Base class for all Activities
 * <p>
 * All kotlin activities must import kotlinx.android.synthetic.main.activity_{name}.* in order to use the ids as the object
 * <p>
 * Any Activity/Fragment implementing controller should override onStart etc and call Controller's start etc
 */

public abstract class CoreActivity extends AppCompatActivity {
    /*Properties*/
    //Back button listeners
    private List<OnBackPressListener> mOnBackPressListeners = new ArrayList<>();

    public final String TAG = getClass().getSimpleName();
    private Toolbar mToolbar;

    public Context getContext() {
        return this;
    }

    @LayoutRes
    protected abstract int layoutId();

    //Return 0 for activity with no container
    @IdRes
    protected abstract int contentContainerId();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(layoutId());
        mToolbar = findViewById(R.id.toolbar);
    }

    /*Toolbar*/
    public void setToolbarTitle(@StringRes int title) {
        setToolbarTitle(getString(title));
    }

    public void setToolbarTitle(String title) {
        LinearLayout doubleContainer = findById(R.id.toolbar_title_double_container);
        View view = findById(R.id.toolbar_title);
        doubleContainer.setVisibility(LinearLayout.GONE);
        view.setVisibility(TextView.VISIBLE);
        if (view != null) {
            ((TextView) view).setText(title);
        }
    }

    public void setDoubleToolbarTitle(String titleTop, String titleBottom){
        LinearLayout doubleContainer = findById(R.id.toolbar_title_double_container);
        View singleTV = findById(R.id.toolbar_title);
        doubleContainer.setVisibility(LinearLayout.VISIBLE);
        singleTV.setVisibility(TextView.GONE);

        TextView viewTop = findById(R.id.toolbar_title_double_top);
        TextView viewBottom = findById(R.id.toolbar_title_double_bottom);

        viewTop.setText(titleTop);
        viewBottom.setText(titleBottom);
    }

    public void setToolbarColor(@ColorRes int color) {
        View view = findById(R.id.toolbar);
        if (view != null) {
            view.setBackgroundColor(ResourcesCompat.getColor(getResources(), color, null));
        }
    }

    public void showRightTextToolbar(String text) {
        TextView view = findById(R.id.toolbar_right_text);
        if (view != null) {
            view.setVisibility(TextView.VISIBLE);
            view.setText(text);
        }
    }

    public void showRightImageToolbar(int resId) {
        ImageView view = findById(R.id.toolbar_right_picture);
        view.setVisibility(ImageView.VISIBLE);
        Picasso.with(this).load(resId).transform(new CircleTransform()).into(view);
    }

    public void showRightImageToolbar(String url){
        ImageView view = findById(R.id.toolbar_right_picture);
        view.setVisibility(ImageView.VISIBLE);
        if (!url.isEmpty()) {
            Picasso.with(this).load(url).transform(new CircleTransform()).placeholder(R.mipmap.ic_launcher_round).into(view);
        }else {
            Picasso.with(this).load(R.mipmap.ic_launcher_round).transform(new CircleTransform()).into(view);
        }
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    /*Navigation*/
    public void showBackArrow(int icon) {

        mToolbar.setNavigationIcon(icon);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void showBackArrow(int icon, boolean finish){
        if(finish){
            mToolbar.setNavigationIcon(icon);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        } else {
            showBackArrow(icon);
        }
    }

    /*Progress Dialog*/
    public void showProgress() {
        View progress = findById(R.id.progress_view);
        if (progress != null) {
            progress.setVisibility(View.VISIBLE);
        }
    }

    public void hideProgress() {
        View progress = findById(R.id.progress_view);
        if (progress != null) {
            progress.setVisibility(View.GONE);
        }
    }

    /*Fragment Control*/
    public void addFragment(@NonNull Fragment fragment, boolean addToBackStack) {
        addFragment(contentContainerId(), fragment, addToBackStack);
    }

    public void replaceFragment(@NonNull Fragment fragment, boolean addToBackStack) {
        replaceFragment(contentContainerId(), fragment, addToBackStack);
    }

    /*Fragment transactions*/
    @SuppressLint("CommitTransaction")
    public void addFragment(@IdRes int containerId, @NonNull Fragment fragment, boolean addToBackStack) {
        String name = fragment.getClass().getName();
        FragmentTransaction add = getSupportFragmentManager().beginTransaction().add(containerId, fragment, name);
        if (addToBackStack) {
            add.addToBackStack(name);
        }
        add.commit();
    }

    @SuppressLint("CommitTransaction")
    public void replaceFragment(@IdRes int containerId, @NonNull Fragment fragment, boolean addToBackStack) {
        String name = fragment.getClass().getName();
        FragmentTransaction replaceTransaction = getSupportFragmentManager().beginTransaction();
        replaceTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        replaceTransaction.replace(containerId, fragment, name);
        if (addToBackStack) {
            replaceTransaction.addToBackStack(name);
        }
        replaceTransaction.commit();
    }

    //ListFragment
    @SuppressLint("CommitTransaction")
    public void replaceFragment(@IdRes int containerId, @NonNull ListFragment fragment, boolean addToBackStack) {
        String name = fragment.getClass().getName();
        android.app.FragmentTransaction replaceTransaction = getFragmentManager().beginTransaction().replace(containerId, fragment, name);
        getFragmentManager().beginTransaction().replace(containerId, fragment, name);
        if (addToBackStack) {
            replaceTransaction.addToBackStack(name);
        }
        replaceTransaction.commit();
    }

    public void removeFragment(@NonNull Fragment fragment) {
        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
    }

    public void removeFragment(@IdRes int id) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(id);
        if (fragment != null) {
            fragmentManager.beginTransaction().remove(fragment).commit();
        }
    }

    public void removeFragment(@NonNull String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment != null) {
            fragmentManager.beginTransaction().remove(fragment).commit();
        }
    }

    public void removeFragment(Class<? extends Fragment> fragmentClass) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(fragmentClass.getName());
        if (fragment != null) {
            fragmentManager.beginTransaction().setCustomAnimations(android.R.animator.fade_out, android.R.animator.fade_in).remove(fragment).commit();
        }
    }

    public void popFragment() {
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack();
    }

    public void closeFromChild() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            supportFinishAfterTransition();
        }
    }

    /*Navigation - Back Button*/
    @Override
    public void onBackPressed() {
        if (interruptedByListener()) {
            //noinspection UnnecessaryReturnStatement
            return;
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    private boolean interruptedByListener() {
        boolean interrupt = false;
        for (OnBackPressListener listener : mOnBackPressListeners) {
            if (listener.onBackPressed()) {
                interrupt = true;
            }
        }
        return interrupt;
    }

    /*Error handling*/
    public void showError(String errorMessage) {
        new AlertDialog.Builder(this).setMessage(errorMessage)
                .setPositiveButton(android.R.string.ok, null).show();
    }

    /*Info feedback*/
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void addOnBackPressListener(OnBackPressListener listener) {
        if (listener != null) {
            mOnBackPressListeners.add(listener);
        }
    }

    public void removeOnBackPressListener(OnBackPressListener listener) {
        mOnBackPressListeners.remove(listener);
    }

    /*Navigation Interface - Back Button*/
    public interface OnBackPressListener {
        boolean onBackPressed();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void close() {
        finishAffinity();
    }

    /*View*/
    @SuppressWarnings("unchecked")
    public <V extends View> V findById(@IdRes int id) {
        return (V) findViewById(id);
    }
}
