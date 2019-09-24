package com.emarsys.config

import com.emarsys.core.api.result.CompletionListener
import com.emarsys.core.storage.Storage
import com.emarsys.mobileengage.MobileEngageInternal
import com.emarsys.mobileengage.MobileEngageRequestContext
import com.emarsys.mobileengage.push.PushInternal
import com.emarsys.mobileengage.push.PushTokenProvider
import com.emarsys.predict.PredictInternal
import com.emarsys.predict.request.PredictRequestContext
import com.emarsys.testUtil.TimeoutUtils
import com.emarsys.testUtil.mockito.whenever
import io.kotlintest.shouldBe
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.util.concurrent.CountDownLatch

class DefaultConfigInternalTest {
    private companion object {
        const val APPLICATION_CODE = "applicationCode"
        const val OTHER_APPLICATION_CODE = "otherApplicationCode"
        const val CONTACT_FIELD_ID = 3
        const val CONTACT_FIELD_VALUE = "originalContactFieldValue"
        const val PUSH_TOKEN = "pushToken"
    }

    private lateinit var configInternal: ConfigInternal
    private lateinit var mockMobileEngageRequestContext: MobileEngageRequestContext
    private lateinit var mockPredictRequestContext: PredictRequestContext
    private lateinit var mockMobileEngageInternal: MobileEngageInternal
    private lateinit var mockPushInternal: PushInternal
    private lateinit var mockPushTokenProvider: PushTokenProvider
    private lateinit var mockPredictInternal: PredictInternal
    private lateinit var mockContactFieldValueStorage: Storage<String>
    private lateinit var mockApplicationCodeStorage: Storage<String?>

    @Rule
    @JvmField
    val timeout: TestRule = TimeoutUtils.timeoutRule

    @Before
    @Suppress("UNCHECKED_CAST")
    fun setUp() {
        mockPushTokenProvider = mock(PushTokenProvider::class.java)
        whenever(mockPushTokenProvider.providePushToken()).thenReturn(PUSH_TOKEN)

        mockContactFieldValueStorage = (mock(Storage::class.java) as Storage<String>).apply {
            whenever(get()).thenReturn(CONTACT_FIELD_VALUE).thenReturn(null)
        }
        mockApplicationCodeStorage = (mock(Storage::class.java) as Storage<String?>).apply {
            whenever(get()).thenReturn(APPLICATION_CODE)
        }

        mockMobileEngageRequestContext = mock(MobileEngageRequestContext::class.java).apply {
            whenever(applicationCodeStorage).thenReturn(mockApplicationCodeStorage)
            whenever(contactFieldValueStorage).thenReturn(mockContactFieldValueStorage)
            whenever(contactFieldId).thenReturn(CONTACT_FIELD_ID)
        }
        mockMobileEngageInternal = mock(MobileEngageInternal::class.java).apply {
            whenever(clearContact(any())).thenAnswer { invocation ->
                mockContactFieldValueStorage.get()
                (invocation.getArgument(0) as CompletionListener).onCompleted(null)
            }
            whenever(setContact(any(), any())).thenAnswer { invocation ->
                (invocation.getArgument(1) as CompletionListener).onCompleted(null)
            }
        }
        mockPushInternal = mock(PushInternal::class.java).apply {
            whenever(setPushToken(any(), any())).thenAnswer { invocation ->
                (invocation.getArgument(1) as CompletionListener).onCompleted(null)
            }
        }

        mockPredictInternal = mock(PredictInternal::class.java)
        mockPredictRequestContext = mock(PredictRequestContext::class.java)

        configInternal = DefaultConfigInternal(mockMobileEngageRequestContext, mockMobileEngageInternal, mockPushInternal, mockPushTokenProvider)
    }

    @Test
    fun testGetContactFieldId_shouldReturnValueFromRequestContext() {
        val result = configInternal.contactFieldId

        result shouldBe CONTACT_FIELD_ID
    }

    @Test
    fun testGetApplicationCode_shouldReturnValueFromRequestContext() {
        val result = configInternal.applicationCode

        result shouldBe APPLICATION_CODE
    }

    @Test
    fun testChangeApplicationCode_shouldCallClearContact() {
        configInternal.changeApplicationCode(OTHER_APPLICATION_CODE, CompletionListener { })

        verify(mockMobileEngageInternal).clearContact(any(CompletionListener::class.java))
    }

    @Test
    fun testChangeApplicationCode_shouldCallSetContactWithOriginalContactFieldIdAndContactFieldValue() {
        configInternal.changeApplicationCode(OTHER_APPLICATION_CODE, CompletionListener {
            verify(mockMobileEngageInternal).clearContact(any(CompletionListener::class.java))
            verify(mockMobileEngageInternal).setContact(eq(CONTACT_FIELD_VALUE), any())
        })
    }

    @Test
    fun testChangeApplicationCode_shouldCallSetPushToken() {
        val latch = CountDownLatch(1)
        configInternal.changeApplicationCode(OTHER_APPLICATION_CODE, CompletionListener {
            latch.countDown()
        })
        latch.await()

        verify(mockMobileEngageInternal).clearContact(any(CompletionListener::class.java))
        verify(mockMobileEngageInternal).setContact(eq(CONTACT_FIELD_VALUE), any())
        verify(mockPushInternal).setPushToken(eq(PUSH_TOKEN), any())
    }
}