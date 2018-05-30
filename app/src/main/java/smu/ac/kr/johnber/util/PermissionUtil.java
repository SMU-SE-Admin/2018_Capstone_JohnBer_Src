package smu.ac.kr.johnber.util;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import static smu.ac.kr.johnber.util.LogUtils.makeLogTag;
/*
- Check the platform(sdk version >=M)
- Check permission
- Explain rationale
- Request permission
- Handle response
 */

public class PermissionUtil {

    private static String TAG = makeLogTag(PermissionUtil.class);

    //권한을 이미 가지고 있는지 체크
    public static boolean shouldAskPermission(Activity activity, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                return true;
        }
        //권한 있음
        return false;
    }

    //해당 권한이 필요한 이유를 설명해야하는가?
    /*
    * 다시보지않기를 체크한 경우 false리턴 -> 앱 설정으로 이동해서 수동으로 허용해야함
    * 처음 권한을 요청하는경우 false
    * 한번 이상 권한 거부한경우 true 리턴
     */
    public static boolean shouldShowRationale(Activity  activity, String permission){
        if (activity.shouldShowRequestPermissionRationale(permission)) {
            return true;
        }
        return false;
    }

    public static void checkPermission(final Activity activity, final String permission, final int requestcode){
        //권한 체크
        if (shouldAskPermission(activity, permission)) {
        //권한 없음

            //이유 설명해야하는가?
            if (shouldShowRationale(activity, permission)) {
                //다이얼로그를 띄워 이유 설명
                  AlertDialog.Builder dialog =
                        new AlertDialog.Builder(activity)
                                .setMessage("앱을 사용하는동안 사용자 위치 접근 권한이 필요합니다.")
                                .setPositiveButton("허용", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //권한 요청
                                        ActivityCompat.requestPermissions(activity, new String [] {permission},requestcode);
                                    }
                                })
                                .setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                });
                  dialog.show();

                  return;
            }else if(!shouldShowRationale(activity, permission) && shouldAskPermission(activity,permission)) {
                //false인경우 - don't ask me agian 택한경우 , permission disabled된 상태  -> 직접 설정으로 이동해야함
                // snackbar 안내문구 -> 설정 activity
                makePermissionRationaleSnackbar(activity);
            }
           //권한 요청
            ActivityCompat.requestPermissions(activity, new String [] {permission},requestcode);
        }
        //권한 있음
    }

    public static Snackbar makePermissionRationaleSnackbar(Activity activity, String message){

         View rootView =  activity.getWindow().getDecorView().findViewById(android.R.id.content);
         Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_LONG);
         snackbar.show();
        return snackbar;
    }
    public static Snackbar makePermissionRationaleSnackbar(final Activity activity){

         View rootView =  activity.getWindow().getDecorView().findViewById(android.R.id.content);
         Snackbar snackbar = Snackbar.make(rootView, "앱 사용 동안 위치 권한 접근이 필요합니다. 설정>권한에서 권한을 허용할 수 있습니다.", Snackbar.LENGTH_LONG)
                                    .setAction("설정", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            //설정페이지로 이동
                                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                                            intent.setData(uri);
                                            activity.startActivity(intent);
                                        }
                                    });
         snackbar.show();
        return snackbar;
    }

}



