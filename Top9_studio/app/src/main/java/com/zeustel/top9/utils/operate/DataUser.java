package com.zeustel.top9.utils.operate;

import android.content.Context;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.zeustel.top9.bean.LoginNote;
import com.zeustel.top9.bean.UserInfo;
import com.zeustel.top9.result.Result;
import com.zeustel.top9.result.UserInfoResult;
import com.zeustel.top9.utils.Constants;
import com.zeustel.top9.utils.NetHelper;
import com.zeustel.top9.utils.SharedPreferencesUtils;
import com.zeustel.top9.utils.SimpleResponseHandler;
import com.zeustel.top9.utils.Tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.NoSuchAlgorithmException;

/**
 * @author NiuLei
 * @email niulei@zeustel.com
 * @date 2015/9/1 16:36
 */
public class DataUser/* extends DataBaseOperate<UserInfo>*/ {


    public static void toLogin(Context context, SimpleResponseHandler httpResponseHandler) throws Exception {
        String token = SharedPreferencesUtils.getToken(context);
        if (Tools.isEmpty(token)) {
            return;
        }
        Constants.USER.setToken(token);
        NetHelper.get(context, Constants.URL_LOGIN_TOKEN, null, new LoginResponseHandler(context, httpResponseHandler));
    }

    public static void toLogin(Context context, UserInfo userInfo, String openId, SimpleResponseHandler httpResponseHandler) {
        if (userInfo == null) {
            return;
        }
        RequestParams params = new RequestParams();
        params.put("loginPlatform", userInfo.getLoginPlatform());
        params.put("nickName", userInfo.getNickName());
        params.put("gender", userInfo.getGender());
        params.put("Icon", userInfo.getIcon());
        params.put("openId", openId);
        NetHelper.post(Constants.URL_LOGIN_THIRD, params, new LoginResponseHandler(context, httpResponseHandler));
    }

    public static void toLogin(Context context, final String account, final String password, SimpleResponseHandler httpResponseHandler) throws Exception {
        NetHelper.post(Constants.URL_LOGIN, createParams(account, password), new LoginResponseHandler(context, httpResponseHandler));
    }

    private static RequestParams createParams(String account, String password) throws Exception {
        final String encryptPassword = Tools.md5Encrypt(password);
        RequestParams params = new RequestParams();
        params.put("username", account);
        params.put("password", encryptPassword);
        return params;
    }

    public static void toRegist(String account, String password, int gender, SimpleResponseHandler httpResponseHandler) throws Exception {
        RequestParams params = createParams(account, password);
        params.put("gender", gender);
        NetHelper.post(Constants.URL_REGIST, params, httpResponseHandler);
    }

    public static void toLoginOut() {
        if (Constants.USER != null) {
            Constants.USER.setIsOnline(false);
            Tools.log_i(DataUser.class, "toLoginOut", "");
        }
    }

    /**
     * 修改昵称
     *
     * @param nickName      昵称
     * @param updateHandler 回调
     */
    public static void updateInfo(String nickName, SimpleResponseHandler updateHandler) {
        RequestParams params = new RequestParams();
        params.put("userId", Constants.USER.getId());
        params.put("type", "NICKNAME");
        params.put("nickName", nickName);
        NetHelper.post(Constants.URL_UPDATE_USER, params, updateHandler);
    }

    /**
     * 修改密码
     *
     * @param password      旧密码
     * @param newPwd        新密码
     * @param updateHandler 回调
     * @throws NoSuchAlgorithmException
     */
    public static void updateInfo(String password, String newPwd, SimpleResponseHandler updateHandler) throws NoSuchAlgorithmException {
        password = Tools.md5Encrypt(password);
        newPwd = Tools.md5Encrypt(newPwd);
        RequestParams params = new RequestParams();
        params.put("userId", Constants.USER.getId());
        params.put("type", "PASSWORD");
        params.put("password", password);
        params.put("newPwd", newPwd);
        NetHelper.post(Constants.URL_UPDATE_USER, params, updateHandler);
    }

    public static void updateInfo(File iconFile, SimpleResponseHandler updateHandler) throws NoSuchAlgorithmException, FileNotFoundException {
        RequestParams params = new RequestParams();
        params.put("userId", Constants.USER.getId());
        params.put("type", "ICON");
        Tools.log_i(DataUser.class, "updateInfo", "" + iconFile.exists());
        Tools.log_i(DataUser.class, "updateInfo", "" + iconFile.getAbsolutePath());
        params.put("imgs", iconFile);
        NetHelper.post(Constants.URL_UPDATE_USER, params, updateHandler);
    }

    private final static class LoginResponseHandler extends SimpleResponseHandler {
        private Context context;
        private SimpleResponseHandler httpResponseHandler;

        public LoginResponseHandler(Context context, SimpleResponseHandler httpResponseHandler) {
            this.context = context;
            this.httpResponseHandler = httpResponseHandler;
        }

        @Override
        public void onCallBack(int code, String json) {
            super.onCallBack(code, json);
            if (httpResponseHandler != null) {
                httpResponseHandler.onCallBack(code, json);
            }
            Gson gson = new Gson();
            UserInfoResult result = gson.fromJson(json, UserInfoResult.class);
            if (result != null) {
                if (Result.SUCCESS == result.getSuccess()) {
                    if (context != null) {
                        UserInfo userInfo = result.getData();
                        if (userInfo != null) {
                            Constants.USER.setEmail(userInfo.getEmail());
                            Constants.USER.setFlowerAmount(userInfo.getFlowerAmount());
                            Constants.USER.setIntegralAmount(userInfo.getIntegralAmount());
                            Constants.USER.setLoginPlatform(userInfo.getLoginPlatform());
                            Constants.USER.setPhone(userInfo.getPhone());
                            Constants.USER.setToken(userInfo.getToken());
                            Constants.USER.setIcon(userInfo.getIcon());
                            Constants.USER.setUsername(userInfo.getUsername());
                            Constants.USER.setGender(userInfo.getGender());
                            Constants.USER.setTime(userInfo.getTime());
                            Constants.USER.setLastModifyTime(userInfo.getLastModifyTime());
                            Constants.USER.setId(userInfo.getId());
                            Constants.USER.setNickName(userInfo.getNickName());
                            Constants.USER.setIsOnline(true);
                            SharedPreferencesUtils.saveUser(context, new LoginNote(userInfo.getUsername(), System.currentTimeMillis(), userInfo.getToken()));
                        }
                    }
                }
            }
        }
    }

    /**
     * 检查是否点赞
     *
     * @param id
     * @param asyncHttpResponseHandler
     */
    public static void checkGoodState(int id, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("userId", Constants.USER.getId());
        params.put("commentId", id);
        NetHelper.post(Constants.URL_CHEKC_GOOD, params, asyncHttpResponseHandler);//callback?
    }

    /**
     * 点赞
     *
     * @param id
     * @param asyncHttpResponseHandler
     */
    public static void doGood(int id, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("userId", Constants.USER.getId());
        params.put("commentId", id);
        NetHelper.post(Constants.URL_GOOD, params, asyncHttpResponseHandler);//callback?
    }
}
