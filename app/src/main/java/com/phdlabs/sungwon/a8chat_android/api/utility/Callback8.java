package com.phdlabs.sungwon.a8chat_android.api.utility;

import com.phdlabs.sungwon.a8chat_android.api.event.Event;
import com.phdlabs.sungwon.a8chat_android.api.response.ErrorResponse;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.ParameterizedType;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by SungWon on 9/18/2017.
 */

public abstract class Callback8<Result, EventClass extends Event> implements Callback<Result> {
    private EventBus eventBus;
    private EventClass eventClass;

    public Callback8(EventBus eventBus, EventClass eventClass) {
        this.eventBus = eventBus;
        this.eventClass = eventClass;
    }

    public Callback8(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @SuppressWarnings({"unchecked", "TryWithIdenticalCatches"})
    private void initParameter() {
        // Get the class name of this instance's type.
        ParameterizedType pt = (ParameterizedType) getClass().getGenericSuperclass();
        // You may need this split or not, use logging to check
        String parameterClassName = pt.getActualTypeArguments()[1].toString().split("\\s")[1];
        // Instantiate the Parameter and initialize it.
        try {
            eventClass = (EventClass) Class.forName(parameterClassName).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponse(Call<Result> call, Response<Result> response) {
        if (response.isSuccessful()) {
            if (response.body() instanceof ErrorResponse) {
                ErrorResponse r = (ErrorResponse) response.body();
                if (r.isSuccess()) {
                    onSuccess(response.body());
                } else {
                    postErrorMessage(r.getMessage());
                }
            } else {
                onSuccess(response.body());
            }
        } else {
            onError(response);
        }
    }

    protected void onError(Response<Result> response) {
        String message = ErrorResponse.fromResponse(response).getMessage();
        postErrorMessage(message);
    }

    protected abstract void onSuccess(Result data);

    @Override
    public void onFailure(Call<Result> call, Throwable t) {
        String message = t.getMessage();
        postErrorMessage(message);
    }

    private void postErrorMessage(String message) {
//        if (eventClass == null) {
//            initParameter();
//        }
//        eventClass.setErrorMessage(message);
//        eventBus.post(eventClass);
    }
}
