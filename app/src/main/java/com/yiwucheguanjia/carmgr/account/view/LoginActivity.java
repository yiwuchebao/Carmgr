package com.yiwucheguanjia.carmgr.account.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.Loader;
import android.database.Cursor;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.andexert.library.RippleView;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.yiwucheguanjia.carmgr.MainActivity;
import com.yiwucheguanjia.carmgr.R;
import com.yiwucheguanjia.carmgr.animation.DiologLoading;
import com.yiwucheguanjia.carmgr.utils.StringCallback;
import com.yiwucheguanjia.carmgr.utils.UrlString;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends FragmentActivity implements View.OnClickListener, LoaderCallbacks<Cursor> {

    private String usernameString;
    private String passwordString;
    //登录状态提示
    private String loginState;
    private Button loginBtn;
    private RippleView gobackRpv;
    private EditText loginUsernameEdit;
    private EditText loginPasswordEdit;
    private TextView loginRegisterTxtBtn;
    private TextView getPassword;
    private TextView loginPhoneTv;
    private ImageView loginQQ;
    private OkHttpClient okHttpClient;
    private DiologLoading diologLoading;
    private int LOGIN_SUSSESS = 3;
    private int LOGIN_ERROR = 4;
    private String flagWhereRequest;//来自于哪个activity发起的登录请求
    private UMShareAPI mShareAPI = null;
    private static final MediaType JSON = MediaType.parse("application/json;charset:utf-8");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // 透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        /** init auth api**/
        setContentView(R.layout.activity_login);
        initView();
        mShareAPI = UMShareAPI.get(LoginActivity.this);
        diologLoading = new DiologLoading(getResources().getString(R.string.logging));
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("action.loginfresh");
        registerReceiver(mRefreshBroadcastReceiver, intentFilter);
    }

    /**
     * 接收来自找回密码界面跳到登录界面传递过来的参数
     */
    protected void getIntenExtra() {
        Bundle bundle = getIntent().getExtras();
        if (!bundle.getString("SETPASSWORD").isEmpty()) {
            flagWhereRequest = bundle.getString("SETPASSWORD");
        }
    }

    private void initView() {
        loginUsernameEdit = (EditText) findViewById(R.id.login_account_edit);
        loginPasswordEdit = (EditText) findViewById(R.id.login_password_edit);
        loginRegisterTxtBtn = (TextView) findViewById(R.id.login_register_btn);
        loginBtn = (Button) findViewById(R.id.login_button);
        gobackRpv = (RippleView) findViewById(R.id.login_back_rl);
        getPassword = (TextView) findViewById(R.id.login_get_pwd_tv);
        loginPhoneTv = (TextView) findViewById(R.id.login_phone_Tv);
        loginQQ = (ImageView) findViewById(R.id.login_qq);
        okHttpClient = new OkHttpClient();
        getPassword.setOnClickListener(this);
        loginRegisterTxtBtn.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        gobackRpv.setOnClickListener(this);
        loginPhoneTv.setOnClickListener(this);
        loginQQ.setOnClickListener(this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Toast.makeText(LoginActivity.this, loginState, Toast.LENGTH_SHORT).show();
                    if (diologLoading != null) {
                        diologLoading.dismiss();
                    }
                    break;
                case 2:
                    //关闭动画
                    if (diologLoading != null) {
                        diologLoading.dismiss();
                    }
//                    setResult(1);
//                    getCallingActivity();
                    Intent intent = new Intent(
                            LoginActivity.this,
                            MainActivity.class);
                    Log.e("version", "version");
                    LoginActivity.this.startActivity(intent);
                    finish();
                    break;
                case 3://请求成功
                    break;
                case 4://请求失败
                    break;
                case 5://appGetConfig方法获取配置请求成功
                    Log.e("config", msg.obj.toString());
                    break;
                case 6://appGetConfig方法获取配置请求失败
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        SHARE_MEDIA platform = null;
        switch (v.getId()) {
            case R.id.login_button:
                if (checkString(loginUsernameEdit, loginPasswordEdit)) {
                    usernameString = loginUsernameEdit.getText().toString().trim();
                    passwordString = loginPasswordEdit.getText().toString().trim();
                    loginAccount(this.usernameString, this.passwordString, "0", "",
                            UUID.randomUUID().toString(), "1.0", UrlString.LOGIN_URL, 1);
                }
                break;
            case R.id.login_register_btn:
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
                break;
            case R.id.login_back_rl:
                this.finish();
                break;
            case R.id.login_get_pwd_tv:
                Intent getPasswordIntent = new Intent(LoginActivity.this, GetPassword.class);
                startActivity(getPasswordIntent);
                break;
            case R.id.login_phone_Tv:
                Intent phoneLogin = new Intent(LoginActivity.this, PhoneLoginActivity.class);
                startActivityForResult(phoneLogin, 2);
//                finish();
                break;
            case R.id.login_qq:
                Log.e("error","that");
                platform = SHARE_MEDIA.QQ;
                /**begin invoke umeng api**/

                mShareAPI.doOauthVerify(LoginActivity.this, platform, umAuthListener);
                break;
            default:
                break;
        }
    }

    /**
     * 核审登录资料
     *
     * @param usernameEdit 用户名
     * @param passwordEdit 登录密码
     * @author huangjunyu
     */
    private Boolean checkString(EditText usernameEdit, EditText passwordEdit) {
        String usernameString = usernameEdit.getText().toString().trim();
        String passwordString = passwordEdit.getText().toString().trim();
        if (usernameString.equals("")) {
            Toast.makeText(LoginActivity.this, getResources().getText(R.string.acc_can_not_null), Toast.LENGTH_SHORT).show();
            return false;
        } else if (passwordString.equals("")) {
            Toast.makeText(LoginActivity.this, getResources().getText(R.string.pwd_can_not_null), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            this.usernameString = usernameString;
            this.passwordString = passwordString;
            return true;
        }

    }

    private void loginAccount(final String username, final String password,
                              String type, String verf_code, String uuid, String version, final String url, int id) {
        diologLoading.show(getSupportFragmentManager(), "login");
        diologLoading.setCancelable(false);
        OkHttpUtils.get().url(url)
                .addParams("username", username)
                .addParams("password", password)
                .addParams("type", type)
                .addParams("verf_code", verf_code)
                .addParams("version", version)
                .id(id)
                .build()
                .execute(new parseStringCallBack());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1) {
        }
    }

    /** auth callback interface**/
    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            Toast.makeText(getApplicationContext(), "Authorize succeed", Toast.LENGTH_SHORT).show();
            com.umeng.socialize.utils.Log.d("user info","user info:"+data.toString());

        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Toast.makeText( getApplicationContext(), "Authorize fail", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toast.makeText( getApplicationContext(), "Authorize cancel", Toast.LENGTH_SHORT).show();
        }
    };

    private class parseStringCallBack extends StringCallback {

        @Override
        public void onError(Call call, Exception e, int id) {

        }

        @Override
        public void onResponse(String response, int id) {
            switch (id) {
                case 1:
                    Log.e("sme", response);
                    if (response != null) {
                        try {
                            JSONObject jsonAll = new JSONObject(response);
                            if (jsonAll.getString("opt_state").equals("fail")) {
                                Log.e("jsonAll", jsonAll + "");
                                loginState = jsonAll.getString("opt_info");
                                handler.sendEmptyMessage(1);
                            } else if (jsonAll.getString("opt_state").equals("success")) {
                                Log.e("token", jsonAll.getString("token"));
                                setSharePrefrence(usernameString, passwordString, jsonAll.getString("token"));
                                Log.e("login", "登录成功" + response);
                                handler.sendEmptyMessage(2);
                            }
                            ;
                        } catch (JSONException e) {
                            Log.e("ooeo", "kaeee");
                            e.printStackTrace();
                        }
                    }
                    break;
                case 2:
                    Log.e("login", response);
                    break;
                default:
                    break;
            }
        }
    }

    private void setSharePrefrence(String account, String password, String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("CARMGR", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString("ACCOUNT", account);
        edit.putString("PASSWORD", password);
        edit.putString("TOKEN", token);
        edit.commit();
    }
    private BroadcastReceiver mRefreshBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("action.loginfresh")) {
                Log.e("ooeo", "ka");
                LoginActivity.this.finish();
            }
        }
    };
    @Override
    protected void onDestroy() { // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(mRefreshBroadcastReceiver);
    }

}

