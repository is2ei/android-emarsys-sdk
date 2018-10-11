package com.emarsys.mobileengage;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.emarsys.core.CoreCompletionHandler;
import com.emarsys.core.DefaultCompletionHandler;
import com.emarsys.core.DeviceInfo;
import com.emarsys.core.concurrency.CoreSdkHandlerProvider;
import com.emarsys.core.provider.timestamp.TimestampProvider;
import com.emarsys.core.provider.uuid.UUIDProvider;
import com.emarsys.core.request.RequestManager;
import com.emarsys.core.request.model.RequestModel;
import com.emarsys.core.response.AbstractResponseHandler;
import com.emarsys.core.response.ResponseModel;
import com.emarsys.mobileengage.config.MobileEngageConfig;
import com.emarsys.mobileengage.storage.AppLoginStorage;
import com.emarsys.mobileengage.storage.MeIdSignatureStorage;
import com.emarsys.mobileengage.storage.MeIdStorage;
import com.emarsys.mobileengage.util.MobileEngageIdlingResource;
import com.emarsys.testUtil.DatabaseTestUtils;
import com.emarsys.testUtil.TimeoutUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
@Ignore
public class MobileEngageInternalIdlingResourceTest {

    private MobileEngageInternal mobileEngage;
    private CoreCompletionHandler coreCompletionHandler;
    private MobileEngageIdlingResource idlingResource;
    private Handler coreSdkHandler;

    @Rule
    public TestRule timeout = TimeoutUtils.getTimeoutRule();

    @Before
    public void init() throws Exception {
        DatabaseTestUtils.deleteMobileEngageDatabase();
        DatabaseTestUtils.deleteCoreDatabase();

        Application application = (Application) InstrumentationRegistry.getTargetContext().getApplicationContext();

        MobileEngageConfig config = new MobileEngageConfig.Builder()
                .application(application)
                .credentials("user", "pass")
                .enableIdlingResource(true)
                .disableDefaultChannel()
                .build();

        coreSdkHandler = new CoreSdkHandlerProvider().provideHandler();

        coreCompletionHandler = new DefaultCompletionHandler(
                new ArrayList<AbstractResponseHandler>());

        MeIdStorage meIdStorage = mock(MeIdStorage.class);
        when(meIdStorage.get()).thenReturn("meId");
        MeIdSignatureStorage meIdSignatureStorage = mock(MeIdSignatureStorage.class);
        when(meIdSignatureStorage.get()).thenReturn("meIdSignature");

        UUIDProvider uuidProvider = mock(UUIDProvider.class);
        when(uuidProvider.provideId()).thenReturn("REQUEST_ID");
        mobileEngage = new MobileEngageInternal(
                mock(RequestManager.class),
                mock(Handler.class),
                mock(DefaultCompletionHandler.class),
                new RequestContext(
                        "applicationCode",
                        "applicationPassword",
                        1,
                        mock(DeviceInfo.class),
                        mock(AppLoginStorage.class),
                        meIdStorage,
                        meIdSignatureStorage,
                        mock(TimestampProvider.class),
                        uuidProvider));

        MobileEngageUtils.setup(config);
        idlingResource = mock(MobileEngageIdlingResource.class);
        Field idlingResourceField = MobileEngageUtils.class.getDeclaredField("idlingResource");
        idlingResourceField.setAccessible(true);
        idlingResourceField.set(null, idlingResource);
        new MeIdStorage(InstrumentationRegistry.getContext()).set("test_me_id");
    }

    @After
    public void tearDown() {
        new MeIdStorage(InstrumentationRegistry.getContext()).remove();
        coreSdkHandler.getLooper().quit();
    }

    @Test
    public void testAppLogin_anonymous_callsIdlingResource() {
        mobileEngage.appLogin();

        verify(idlingResource, times(1)).increment();
    }

    @Test
    public void testAppLogin_callsIdlingResource() {
        mobileEngage.appLogin(3, "test@test.com");

        verify(idlingResource, times(1)).increment();
    }

    @Test
    public void testAppLogout_callsIdlingResource() {
        mobileEngage.appLogout();

        verify(idlingResource, times(1)).increment();
    }

    @Test
    public void testTrackCustomEvent_callsIdlingResource() {
        mobileEngage.trackCustomEvent("eventName", null);

        verify(idlingResource, times(1)).increment();
    }

    @Test
    public void testTrackCustomEvent_withAttributes_callsIdlingResource() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("key", "value");
        mobileEngage.trackCustomEvent("eventName", attributes);

        verify(idlingResource, times(1)).increment();
    }

    @Test
    public void testTrackInternalCustomEvent_callsIdlingResource() {
        mobileEngage.trackInternalCustomEvent("eventName", null);

        verify(idlingResource, times(1)).increment();
    }

    @Test
    public void testTrackInternalCustomEvent_withAttributes_callsIdlingResource() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("key", "value");
        mobileEngage.trackInternalCustomEvent("eventName", attributes);

        verify(idlingResource, times(1)).increment();
    }

    @Test
    public void testTrackMessageOpen_emptyIntent_doesNotCallIdlingResource() {
        mobileEngage.trackMessageOpen(new Intent());

        verifyZeroInteractions(idlingResource);
    }

    @Test
    public void testTrackMessageOpen_correctIntent_callsIdlingResource() {
        Intent intent = new Intent();
        Bundle bundlePayload = new Bundle();
        bundlePayload.putString("key1", "value1");
        bundlePayload.putString("u", "{\"sid\": \"+43c_lODSmXqCvdOz\"}");
        intent.putExtra("payload", bundlePayload);

        mobileEngage.trackMessageOpen(intent);

        verify(idlingResource, times(1)).increment();
    }

    @Test
    public void testCoreCompletionHandler_onSuccess_callsIdlingResource() {
        coreCompletionHandler.onSuccess(
                "id",
                new ResponseModel.Builder()
                        .statusCode(200)
                        .message("OK")
                        .requestModel(mock(RequestModel.class))
                        .build());

        verify(idlingResource, times(1)).decrement();
    }

    @Test
    public void testCoreCompletionHandler_onError_responseModel_callsIdlingResource() {
        coreCompletionHandler.onError(
                "id",
                new ResponseModel.Builder()
                        .statusCode(404)
                        .message("Not found")
                        .requestModel(mock(RequestModel.class))
                        .build());

        verify(idlingResource, times(1)).decrement();
    }

    @Test
    public void testCoreCompletionHandler_onError_exception_callsIdlingResource() {
        coreCompletionHandler.onError("id", new Exception("exception"));

        verify(idlingResource, times(1)).decrement();
    }

}