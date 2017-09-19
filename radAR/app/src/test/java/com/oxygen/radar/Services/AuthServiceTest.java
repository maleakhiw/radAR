package com.oxygen.radar.Services;

import android.content.Context;
import android.content.SharedPreferences;
import android.test.mock.MockContext;

import com.oxygen.radar.Models.Requests.SignUpRequest;
import com.oxygen.radar.Models.Responses.AuthResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyInt;

/**
 * Created by kenneth on 18/9/17.
 */
public class AuthServiceTest {
    AuthService authService;
    AuthApi mockAuthApi;
    Context mockContext;
    SharedPreferences mockSharedPrefs;
    SharedPreferences.Editor mockEditor;

    @BeforeClass
    public static void setupClass() {
        // set all schedulers to trampoline scheduler
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(
                __ -> Schedulers.trampoline());
        RxJavaPlugins.setIoSchedulerHandler(
                scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setComputationSchedulerHandler(
                scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setNewThreadSchedulerHandler(
                scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(
                scheduler -> Schedulers.trampoline());

    }

    @Before
    public void setUp() throws Exception {
        // mock the Retrofit API
        mockAuthApi = Mockito.mock(AuthApi.class);

        // mock Android's SharedPreferences
        mockSharedPrefs = Mockito.mock(SharedPreferences.class);
        mockEditor = Mockito.mock(SharedPreferences.Editor.class);
        Mockito.when(mockSharedPrefs.edit()).thenReturn(mockEditor);
        Mockito.when(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor);
        Mockito.when(mockEditor.putInt(anyString(), anyInt())).thenReturn(mockEditor);

        // mock the Android Context
        mockContext = Mockito.mock(Context.class);
        Mockito.when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockSharedPrefs);

        AuthResponse authResponse = new AuthResponse();
        authResponse.success = true;
        authResponse.errors = new ArrayList<>();
        authResponse.token = "someString";
        authResponse.userID = 3141592;

        Mockito.when(mockAuthApi.signUp(Mockito.any(SignUpRequest.class))).
                thenReturn(Observable.just(new AuthResponse()));
        authService = new AuthService(mockAuthApi, mockContext);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getToken() throws Exception {
    }

    @Test
    public void getUserID() throws Exception {
    }

    @Test
    public void signUp() throws Exception {
        authService.signUp(new SignUpRequest("first", "last", "email@example.com", "username", "", "hunter2", "fake"));
        Mockito.verify(mockAuthApi).signUp(Mockito.any(SignUpRequest.class));   // make sure AuthApi.signUp is called
        Mockito.verify(mockEditor).putString(anyString(), anyString());
        Mockito.verify(mockEditor).putInt(anyString(), anyInt());
    }

    @Test
    public void login() throws Exception {
    }

}