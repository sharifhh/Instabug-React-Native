package com.instabug.reactlibrary;

import android.os.Looper;
import com.facebook.react.bridge.JavaOnlyArray;
import com.instabug.bug.BugReporting;
import com.instabug.library.Feature;
import com.instabug.library.extendedbugreport.ExtendedBugReport;
import com.instabug.library.invocation.InstabugInvocationEvent;
import com.instabug.library.invocation.util.InstabugVideoRecordingButtonPosition;
import com.instabug.reactlibrary.utils.MainThreadHandler;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;



public class RNInstabugBugReportingModuleTest {

    private RNInstabugBugReportingModule bugReportingModule = new RNInstabugBugReportingModule(null);
    private final static ScheduledExecutorService mainThread = Executors.newSingleThreadScheduledExecutor();

    // Mock Objects
    private MockedStatic<Looper> mockLooper;
    private MockedStatic <MainThreadHandler> mockMainThreadHandler;
    private MockedStatic <BugReporting> mockBugReporting;

    @Before
    public void mockMainThreadHandler() throws Exception {
        // Mock static functions
        mockBugReporting = mockStatic(BugReporting.class);
        mockLooper = mockStatic(Looper.class);
        mockMainThreadHandler = mockStatic(MainThreadHandler.class);

        // Mock Looper class
        Looper mockMainThreadLooper = mock(Looper.class);
        when(Looper.getMainLooper()).thenReturn(mockMainThreadLooper);

        // Override runOnMainThread
        Answer<Boolean> handlerPostAnswer = new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                invocation.getArgument(0, Runnable.class).run();
                return true;
            }
        };
        doAnswer(handlerPostAnswer).when(MainThreadHandler.class);
        MainThreadHandler.runOnMainThread(any(Runnable.class));
    }
    @After
    public void tearDown() {
        // Remove static mocks
        mockLooper.close();
        mockMainThreadHandler.close();
        mockBugReporting.close();
    }

    /********BugReporting*********/


    @Test
    public void givenShakingThreshold$setShakingThresholdForAndroid_whenQuery_thenShouldCallNativeApiWithShakingThreshold() {
        // given
        int shakingThreshold = 30;
        // when
        bugReportingModule.setShakingThresholdForAndroid(shakingThreshold);
        // then
        verify(BugReporting.class,VerificationModeFactory.times(1));
        BugReporting.setShakingThreshold(shakingThreshold);
    }

    @Test
    public void givenFalse$setBugReportingEnabled_whenQuery_thenShouldCallNativeApiWithDisabled() {
        // given

        // when
        bugReportingModule.setEnabled(false);
        // then
        verify(BugReporting.class,VerificationModeFactory.times(1));

        BugReporting.setState(Feature.State.DISABLED);
    }

    @Test
    public void givenTrue$setBugReportingEnabled_whenQuery_thenShouldCallNativeApiWithEnabled() {
        // given

        // when
        bugReportingModule.setEnabled(true);
        // then
        verify(BugReporting.class,VerificationModeFactory.times(1));

        BugReporting.setState(Feature.State.ENABLED);
    }

    @Test
    public void givenIsEnabled$setAutoScreenRecordingEnabled_whenQuery_thenShouldCallNativeApiWithIsEnabled() {
        // given

        // when
        bugReportingModule.setAutoScreenRecordingEnabled(true);
        // then
        verify(BugReporting.class,VerificationModeFactory.times(1));

        BugReporting.setAutoScreenRecordingEnabled(true);
    }

    @Test
    public void givenTrue$setViewHierarchyEnabled_whenQuery_shouldCallNativeApiWithEnabled() {
        // given

        // when
        bugReportingModule.setViewHierarchyEnabled(true);
        // then
        verify(BugReporting.class,VerificationModeFactory.times(1));

        BugReporting.setViewHierarchyState(Feature.State.ENABLED);
    }

    @Test
    public void givenFalse$setViewHierarchyEnabled_whenQuery_shouldCallNativeApiWithDisabled() {
        // given

        // when
        bugReportingModule.setViewHierarchyEnabled(false);
        // then
        verify(BugReporting.class,VerificationModeFactory.times(1));

        BugReporting.setViewHierarchyState(Feature.State.DISABLED);
    }

    @Test
    public void givenBooleanArgs$setEnabledAttachmentTypes_whenQuery_shouldCallNativeApiWithBooleanArgs() {
        // given

        // when
        bugReportingModule.setEnabledAttachmentTypes(true, true, false, true);
        // then
        verify(BugReporting.class,VerificationModeFactory.times(1));

        BugReporting.setAttachmentTypesEnabled(true, true, false, true);
    }

    @Test
    public void givenExtendedBugReportMode$setExtendedBugReportMode_whenQuery_thenShouldCallNativeApiWithArgs() {
        // given

        Map<String, Object> args = new HashMap<>();
        ArgsRegistry.registerInstabugExtendedBugReportModeArgs(args);
        // when
        for (String strExtendedBugReportMode : args.keySet()) {
            bugReportingModule.setExtendedBugReportMode(strExtendedBugReportMode);
        }
        // then
        for (Object extendedBugReportMode : args.values()) {
            ExtendedBugReport.State state = (ExtendedBugReport.State) extendedBugReportMode;
            verify(BugReporting.class,VerificationModeFactory.times(1));

            BugReporting.setExtendedBugReportState(state);
        }
    }

    @Test
    public void givenInvocationEvent$setInvocationEvents_whenQuery_thenShouldCallNativeApiWithArgs() {
        // given

        final Map<String, Object> args = new HashMap<>();
        ArgsRegistry.registerInstabugInvocationEventsArgs(args);
        final String[] keysArray = args.keySet().toArray(new String[0]);
        JavaOnlyArray actualArray = new JavaOnlyArray();
        for (String key : keysArray) {
            actualArray.pushString(key);
        }
        // when
        bugReportingModule.setInvocationEvents(actualArray);
        // then
        verify(BugReporting.class,VerificationModeFactory.times(1));

        BugReporting.setInvocationEvents(args.values().toArray(new InstabugInvocationEvent[0]));
    }

    @Test
    public void givenOptions$setOptions_whenQuery_thenShouldCallNativeApiWithArgs() {
        // given

        final Map<String, Object> args = new HashMap<>();
        ArgsRegistry.registerInvocationOptionsArgs(args);
        final String[] keysArray = args.keySet().toArray(new String[0]);
        JavaOnlyArray actualArray = new JavaOnlyArray();
        actualArray.pushString(keysArray[0]);
        actualArray.pushString(keysArray[1]);
        // when
        bugReportingModule.setOptions(actualArray);
        // then
        verify(BugReporting.class,VerificationModeFactory.times(1));

        int option1 = (int) args.get(keysArray[0]);
        int option2 = (int) args.get(keysArray[1]);
        BugReporting.setOptions(option1);
        verify(BugReporting.class,VerificationModeFactory.times(1));

        BugReporting.setOptions(option2);
    }

//    @Test
//    public void given$setOnInvokeHandler_whenQuery_thenShouldSetNativeCallback() {
//
//        try {
//            // given
//    
//            mockStatic(InstabugUtil.class);
//            // when
//            doAnswer(new Answer<Object>() {
//                @Override
//                public Object answer(InvocationOnMock invocation) {
//                    ((OnInvokeCallback) invocation.getArguments()[0]).onInvoke();
//                    return null;
//                }
//            }).when(BugReporting.class);
//            BugReporting.setOnDismissCallback(anyObject());
//            bugReportingModule.setOnInvokeHandler(null);
//            // then
//            verify(InstabugUtil.class,VerificationModeFactory.times(1));
//
//            InstabugUtil.sendEvent(any(ReactApplicationContext.class), eq(Constants.IBG_PRE_INVOCATION_HANDLER), Matchers.isNull(WritableMap.class));
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//

//    @Test
//    public void given$setOnSDKDismissedHandler_whenQuery_thenShouldSetNativeCallback() {
//        try {
//            // given
//            mockStatic(Arguments.class);
//
//            // when
//            when(Arguments.createMap()).thenReturn(new JavaOnlyMap());
//            doAnswer(new Answer<Object>() {
//                @Override
//                public Object answer(InvocationOnMock invocation) {
//                    mockStatic(InstabugUtil.class);
//                    doNothing().when(InstabugUtil.class);
//                    InstabugUtil.sendEvent(any(),any(),any());
//                    ((OnSdkDismissCallback) invocation.getArguments()[0])
//                            .call(OnSdkDismissCallback.DismissType.CANCEL, OnSdkDismissCallback.ReportType.BUG);
//                    return null;
//                }
//            }).when(BugReporting.class);
//            BugReporting.setOnDismissCallback(anyObject());
//            bugReportingModule.setOnSDKDismissedHandler(null);
//            // then
//            WritableMap params = new JavaOnlyMap();
//            params.putString("dismissType", OnSdkDismissCallback.DismissType.CANCEL.toString());
//            params.putString("reportType", OnSdkDismissCallback.ReportType.BUG.toString());
//            verify(InstabugUtil.class,VerificationModeFactory.times(1));
//
//            InstabugUtil.sendEvent(any(ReactApplicationContext.class), eq(Constants.IBG_POST_INVOCATION_HANDLER), eq(params));
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @Test
    public void givenArray$setReportTypes_whenQuery_thenShouldCallNativeApiWithEnumArgs() {
        // given

        final Map<String, Object> args = new HashMap<>();
        ArgsRegistry.registerInstabugReportTypesArgs(args);
        final String[] keysArray = args.keySet().toArray(new String[0]);
        JavaOnlyArray actualArray = new JavaOnlyArray();
        actualArray.pushString(keysArray[0]);
        actualArray.pushString(keysArray[1]);
        // when
        bugReportingModule.setReportTypes(actualArray);
        // then
        verify(BugReporting.class,VerificationModeFactory.times(1));

        int type1 = (int) args.get(keysArray[0]);
        int type2 = (int) args.get(keysArray[1]);
        BugReporting.setReportTypes(type1, type2);
    }

    @Test
    public void givenString$setVideoRecordingFloatingButtonPosition_whenQuery_thenShouldCallNativeApi() {
        // given

        final Map<String, Object> args = new HashMap<>();
        ArgsRegistry.registerInstabugVideoRecordingFloatingButtonPositionArgs(args);
        final String[] keysArray = args.keySet().toArray(new String[0]);
        // when
        bugReportingModule.setVideoRecordingFloatingButtonPosition(keysArray[0]);
        // then
        verify(BugReporting.class,VerificationModeFactory.times(1));

        InstabugVideoRecordingButtonPosition position = (InstabugVideoRecordingButtonPosition) args.get(keysArray[0]);
        BugReporting.setVideoRecordingFloatingButtonPosition(position);
    }

    @Test
    public void givenArgs$showBugReportingWithReportTypeAndOptions_whenQuery_thenShouldCallNativeApiWithEnums() {
        // given

        final Map<String, Object> optionsArgs = new HashMap<>();
        final Map<String, Object> reportTypeArgs = new HashMap<>();
        ArgsRegistry.registerInvocationOptionsArgs(optionsArgs);
        ArgsRegistry.registerInstabugReportTypesArgs(reportTypeArgs);
        final String[] keysArray = optionsArgs.keySet().toArray(new String[0]);
        final String[] reportTypeKeys = reportTypeArgs.keySet().toArray(new String[0]);
        JavaOnlyArray actualArray = new JavaOnlyArray();
        actualArray.pushString(keysArray[0]);
        actualArray.pushString(keysArray[1]);
        // when
        bugReportingModule.show(reportTypeKeys[0], actualArray);
        // then
        int option1 = (int) optionsArgs.get(keysArray[0]);
        int option2 = (int) optionsArgs.get(keysArray[1]);
        verify(BugReporting.class,VerificationModeFactory.times(1));

        BugReporting.setOptions(option1);
        verify(BugReporting.class,VerificationModeFactory.times(1));

        BugReporting.setOptions(option2);
        verify(BugReporting.class,VerificationModeFactory.times(1));

        BugReporting.show((int) reportTypeArgs.get(reportTypeKeys[0]));
    }

}
