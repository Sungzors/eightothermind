package com.phdlabs.sungwon.a8chat_android.structure.core;

import android.annotation.SuppressLint;
import android.app.ListFragment;
import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SungWon on 9/11/2017.
 * Base class for all Activities
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

    @IdRes
    protected abstract int contentContainerId();

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

    /*View*/
    @SuppressWarnings("unchecked")
    public <V extends View> V findById(@IdRes int id) {
        return (V)  findViewById(id);
    }
}
