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
import android.widget.TextView;

import com.phdlabs.sungwon.a8chat_android.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SungWon on 9/11/2017.
 * Base class for all Activities
 *
 * All kotlin activities must import kotlinx.android.synthetic.main.activity_{name}.* in order to use the ids as the object
 *
 * Any Activity/Fragment implementing controller should override onStart etc and call Controller's start etc
 */

public abstract class CoreActivity extends AppCompatActivity{
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
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    /*Toolbar*/
    public void setToolbarTitle(@StringRes int title) {
        setToolbarTitle(getString(title));
    }

    public void setToolbarTitle(String title) {
        View view = findById(R.id.toolbar_title);
        if (view != null) {
            ((TextView) view).setText(title);
        }
    }

    public void setToolbarColor(@ColorRes int color) {
        View view = findById(R.id.toolbar);
        if (view != null) {
            view.setBackgroundColor(ResourcesCompat.getColor(getResources(), color, null));
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
        FragmentTransaction replaceTransaction = getSupportFragmentManager().beginTransaction()
                .replace(containerId, fragment, name);
        if(addToBackStack){
            replaceTransaction.addToBackStack(name);
        }
        replaceTransaction.commit();
    }

    //ListFragment
    @SuppressLint("CommitTransaction")
    public void replaceFragment(@IdRes int containerId, @NonNull ListFragment fragment, boolean addToBackStack){
        String name = fragment.getClass().getName();
        android.app.FragmentTransaction replaceTransaction = getFragmentManager().beginTransaction().replace(containerId,fragment,name);
        getFragmentManager().beginTransaction().replace(containerId,fragment,name);
        if(addToBackStack){
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
            fragmentManager.beginTransaction().remove(fragment).commit();
        }
    }

    public void popFragment() {
        getSupportFragmentManager().popBackStack();
    }

    public void closeFromChild() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            supportFinishAfterTransition();
        }
    }

    /*Navigation - Back Button*/
    @Override public void onBackPressed() {
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
            if(listener.onBackPressed()){
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

    public void close() {
        finishAffinity();
    }

    /*View*/
    @SuppressWarnings("unchecked")
    public <V extends View> V findById(@IdRes int id) {
        return (V)  findViewById(id);
    }
}
