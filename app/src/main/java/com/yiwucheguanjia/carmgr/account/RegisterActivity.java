package com.yiwucheguanjia.carmgr.account;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yiwucheguanjia.carmgr.R;
import com.yiwucheguanjia.carmgr.personal.personalActivity;
import com.yiwucheguanjia.carmgr.utils.OkhttpManager;
import com.yiwucheguanjia.carmgr.utils.UrlString;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/6/25.
 */
public class RegisterActivity extends Activity implements View.OnClickListener {
//    private static final String APP_KEY = "1442cff959563";
//    private static final String APP_SECRET = "0c0b28ae632b7c05021da8881af48aac";
    private String phoneNumString;
    private ImageButton gobackImgBtn;
    private String usernameString;
    private  String passwordString;
    private RelativeLayout inputPhoneNumRl;
    private RelativeLayout inputCodeRl;
    private RelativeLayout inputPassword;
    private TextView register_number_txt;
    private ImageView register_first_img;
    private TextView register_code_txt;
    private ImageView register_second_img;
    private TextView register_setting_pwd;
    private ImageView register_three_img;
    private TextView register_code_sent;
    private EditText register_number_edit;
    private EditText register_pwd_edit;
    private EditText register_pwd_again;
    private TextView userAgreement;//用户协议
    private View register_edit_divide;
    private Button register_button;
    OkHttpClient client = new OkHttpClient();
    private int step = 1;//用来统计注册的第几步骤
    private Boolean checkUserService = true;//用户协定
    private String account;
    EventHandler eventHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
//        SMSSDK.initSDK(this, APP_KEY, APP_SECRET, false);
        initView();
    }

    private void initView() {
        gobackImgBtn = (ImageButton) findViewById(R.id.register_goback_imgbtn);
        register_number_txt = (TextView) findViewById(R.id.register_number_txt);
        register_first_img = (ImageView) findViewById(R.id.register_first_img);
        register_code_txt = (TextView) findViewById(R.id.register_code_txt);
        register_second_img = (ImageView) findViewById(R.id.register_second_img);
        register_setting_pwd = (TextView) findViewById(R.id.register_setting_pwd);
        register_three_img = (ImageView) findViewById(R.id.register_three_img);
        register_code_sent = (TextView) findViewById(R.id.register_code_sent);
        register_number_edit = (EditText) findViewById(R.id.register_number_edit);
        register_pwd_edit = (EditText) findViewById(R.id.register_pwd_edit);
        register_pwd_again = (EditText) findViewById(R.id.register_pwd_again);
        register_edit_divide = (View) findViewById(R.id.register_edit_divide);
        userAgreement = (TextView) findViewById(R.id.yiwu_agreement_txt);
        register_button = (Button) findViewById(R.id.register_button);
        register_button.setOnClickListener(this);
        gobackImgBtn.setOnClickListener(this);
        userAgreement.setOnClickListener(this);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private String getPhoneNum() {
//        initSDK();
        //判断是不是手机号码
            String phoneNumberStr = register_number_edit.getText().toString().trim();
        if (!isMobileNO(phoneNumberStr)) {
            Toast.makeText(this,"手机号码不符合格式",Toast.LENGTH_SHORT).show();
            return null;
        } else if (isMobileNO(phoneNumberStr)){
            FormBody formBody = new FormBody.Builder()
                    .add("username",phoneNumberStr)
                    .add("type","0")
                    .add("version",UrlString.APP_VERSION)
                    .build();
            sendverfCode(formBody);
            step = 2;
            register_code_sent.setVisibility(View.VISIBLE);
            register_number_txt.setTextColor(getResources().getColor(R.color.gray_default));
//            register_first_img.setImageDrawable(getDrawable(R.mipmap.register_right_nor));
            register_first_img.setImageResource(R.mipmap.register_right_nor);
            register_code_txt.setTextColor(getResources().getColor(R.color.orange));
            register_second_img.setImageResource(R.mipmap.register_right_pre);
//            register_number_edit.getText();
            register_number_edit.setText("");
            register_number_edit.setHint(R.string.msg_code);
            register_button.setText(R.string.submit_code);
        }
        if (!register_number_edit.getText().toString().trim().equals("") && step == 2) {
            step = 3;
            register_code_sent.setVisibility(View.GONE);
            register_number_edit.setVisibility(View.GONE);
            register_pwd_edit.setVisibility(View.VISIBLE);
            register_pwd_again.setVisibility(View.VISIBLE);
            register_edit_divide.setVisibility(View.VISIBLE);
            register_code_txt.setTextColor(getResources().getColor(R.color.gray_default));
            register_second_img.setImageResource(R.mipmap.register_right_nor);
            register_setting_pwd.setTextColor(getResources().getColor(R.color.orange));
            register_three_img.setImageResource(R.mipmap.register_right_pre);
            register_button.setText(R.string.register_account);
            //当两个编辑框的字符串相同时方可提交数据
        } else if (checkString(register_pwd_edit, register_pwd_again)) {
            try {
                registerAccount("15078888888", "000", "15078888888", UrlString.REGISTER_URL);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 注册回调监听接口

        return null;
    }
    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobiles) {
    /*
    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、180、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
        String telRegex = "[1][358]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles)) return false;
        else return mobiles.matches(telRegex);
    }
    private void sendverfCode(FormBody formBody){
        OkhttpManager.getInstance().OKhttpPost(UrlString.APP_SENDVERF_CODE,handler,formBody,1,2);
    }

    private void setSharePrefrence(String account, String password, String phoneNum) {
        SharedPreferences p = getSharedPreferences("CARMGR", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = p.edit();
        edit.putString("ACCOUNT", account);
        edit.putString("PASSWORD", password);
        edit.putString("PHONE", phoneNum);
//        edit.putString("APPKEY", APPKEY);
//        edit.putString("APPSECRET", APPSECRET);
        edit.commit();
    }

    private Boolean checkString(EditText accountEdit, EditText checkPasswordEdit) {
        String accountContain = accountEdit.getText().toString().trim();
        String passwordContain = checkPasswordEdit.getText().toString().trim();
        if (checkUserService && step == 3) {
            if (accountContain.equals("")) {
                Toast.makeText(RegisterActivity.this, getResources().getText(R.string.pwd_can_not_null), Toast.LENGTH_SHORT).show();
                return false;
            } else if (accountContain.length() < 1) {
                Toast.makeText(RegisterActivity.this, getResources().getText(R.string.pwd_length_below), Toast.LENGTH_SHORT).show();
                return false;
            } else if (accountContain.length() > 8) {
                Toast.makeText(RegisterActivity.this, getResources().getText(R.string.pwd_length_overtop), Toast.LENGTH_SHORT).show();
                return false;
            } else if (!passwordContain.equals(accountContain)) {
                Toast.makeText(RegisterActivity.this, getResources().getText(R.string.pwd_inconsistency), Toast.LENGTH_SHORT).show();
                return false;
            } else {
                usernameString = accountContain;
                passwordString = passwordContain;
                setSharePrefrence(usernameString,passwordString,usernameString);
                return true;
            }
        }

        return false;
    }

    //    /*OkHttpClient client = new OkHttpClient();
//RequestBody requestBody = new FormBody()
    private void registerAccount(final String username, final String password, final String mobile, final String url) throws IOException {

        FormBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .add("mobile", mobile)
                .add("terminal_os", "Android")
                .add("terminal_type", Build.MODEL)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
//                    Log.e("json", response.body().string());
                    try {
                        JSONObject responseJson = new JSONObject(response.body().string());
                        if (responseJson.getBoolean("opt_state")){
                            setSharePrefrence(username,password,mobile);
                            Intent personalIntent = new Intent(RegisterActivity.this,personalActivity.class);
                            startActivity(personalIntent);
                            finish();
                        }else {
                            Toast.makeText(RegisterActivity.this,responseJson.getString("opt_info"),Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {

                    throw new IOException("Unexpected code " + response);
                }
            }
        });
    }

    //    public boolean handleMessage()
    private void initSDK() {
        eventHandler = new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                Log.e("sendSussessful", "succeed222");
                handler.sendMessage(msg);
            }
        };
        SMSSDK.registerEventHandler(eventHandler);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_button:
                getPhoneNum();
                break;
            case R.id.register_goback_imgbtn:
                this.finish();
                break;
            case R.id.yiwu_agreement_txt:
                Intent userAgreementInten = new Intent(RegisterActivity.this,UserAgreement.class);
                startActivity(userAgreementInten);
                break;
            default:
                break;
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    Log.e("code",msg.obj.toString());
                    break;
                case 2:
                    break;
                default:
                    break;
            }
//            int event = msg.arg1;
//            int result = msg.arg2;
//            Object data = msg.obj;
//            if (result == SMSSDK.RESULT_COMPLETE) {
//                Log.e("sendSussessful", "succeed");
//                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
//                    Log.e("验证成功", "yes");
//                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
//                    Log.e("MSG HADE SENDED", "SENDED");
//                }
//            } else {
//                Log.e("异常", "except" + event);
//                ((Throwable) data).printStackTrace();
//            }
//            SMSSDK.unregisterEventHandler(eventHandler);
        }
    };
}