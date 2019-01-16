package com.luminousinfoways.makemyreport.Acra;

import android.app.Application;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

/**
 * Created by luminousinfoways on 19/08/15.
 */

/*@ReportsCrashes(
        formUri = "https://medo.cloudant.com/acra-example/_design/acra-storage/_update/report",
        reportType = HttpSender.Type.JSON,
        httpMethod = HttpSender.Method.POST,
        formUriBasicAuthLogin = "tubtakedstinumenterences",
        formUriBasicAuthPassword = "igqMFFMatvtMXVCKgy7u6a5W",
        formKey = "", // This is required for backward compatibility but not used
        customReportContent = {
                ReportField.APP_VERSION_CODE,
                ReportField.APP_VERSION_NAME,
                ReportField.ANDROID_VERSION,
                ReportField.PACKAGE_NAME,
                ReportField.REPORT_ID,
                ReportField.BUILD,
                ReportField.STACK_TRACE
        },
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.toast_crash
)*/
@ReportsCrashes()
public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // The following line triggers the initialization of ACRA
        ACRA.init(this);

        // instantiate the report sender with the email credentials.
        // these will be used to send the crash report
        ACRAReportSender reportSender = new ACRAReportSender("mmrluminousinfoways@gmail.com", "lipl123!@#");

        // register it with ACRA.
        ACRA.getErrorReporter().setReportSender(reportSender);
    }
}