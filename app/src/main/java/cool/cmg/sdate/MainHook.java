package cool.cmg.sdate;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

import android.widget.TextView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage {
    private static final String SYSTEM_UI = "com.android.systemui";
    private static final String QS_CLOCK_INDICATOR_VIEW = SYSTEM_UI + ".statusbar.policy.QSClockIndicatorView";
    private static final String QS_CLOCK_BELL_SOUND = SYSTEM_UI + ".statusbar.policy.QSClockBellSound";
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("Ed", Locale.SIMPLIFIED_CHINESE);

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!SYSTEM_UI.equals(lpparam.packageName)) {
            return;
        }
        try {
            Class<?> qsClockClass = findClass(QS_CLOCK_INDICATOR_VIEW, lpparam.classLoader);
            Class<?> qsClockBellSoundClass = findClass(QS_CLOCK_BELL_SOUND, lpparam.classLoader);
            findAndHookMethod(qsClockClass, "notifyTimeChanged", qsClockBellSoundClass, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                    TextView mClock = (TextView) param.thisObject;
                    String date = LocalDate.now().format(dateFormatter).substring(1);
                    String timeDate = mClock.getText().toString() + " " + date;
                    mClock.setText(timeDate);
                }
            });
        } catch (Exception e) {
            XposedBridge.log(e);
        }
    }
}
