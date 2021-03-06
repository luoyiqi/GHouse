package co.lujun.ghouse.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.whilu.library.CustomRippleButton;
import com.j256.ormlite.dao.Dao;
import com.rey.material.app.Dialog;
import com.rey.material.app.SimpleDialog;
import com.squareup.picasso.Picasso;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.lujun.ghouse.GlApplication;
import co.lujun.ghouse.R;
import co.lujun.ghouse.bean.BaseJson;
import co.lujun.ghouse.bean.Bill;
import co.lujun.ghouse.bean.Config;
import co.lujun.ghouse.bean.House;
import co.lujun.ghouse.bean.Image;
import co.lujun.ghouse.bean.SignCarrier;
import co.lujun.ghouse.bean.User;
import co.lujun.ghouse.ui.event.BaseSubscriber;
import co.lujun.ghouse.ui.widget.LoadingWindow;
import co.lujun.ghouse.ui.widget.roundedimageview.RoundedImageView;
import co.lujun.ghouse.util.DatabaseHelper;
import co.lujun.ghouse.util.IntentUtils;
import co.lujun.ghouse.util.MD5;
import co.lujun.ghouse.util.NetWorkUtils;
import co.lujun.ghouse.util.PreferencesUtils;
import co.lujun.ghouse.util.SignatureUtil;
import co.lujun.ghouse.util.SystemUtil;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by lujun on 2015/7/30.
 */
public class CenterActivity extends BaseActivity {

    private Toolbar mToolbar;
    private RoundedImageView ivAvatar;
    private TextView tvUName, tvPhone, tvHouseId;
    private RelativeLayout llPhone, llHouseId, llUpdatePwd;
    private CustomRippleButton btnLogout;

    private TextInputLayout tilPhone, tilOldPwd, tilNewPwd;
    private View updatePhoneView, updatePwdView;

    private static Dialog mUpdatePhoneDialog, mUpdatePwdDialog;

    private static final String TAG = "CenterActivity";

    private LoadingWindow winLoading;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_center);
        init();
    }

    private void init(){
        mToolbar = (Toolbar) findViewById(R.id.tb_center);
        setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ivAvatar = (RoundedImageView) findViewById(R.id.iv_set_avatar);
        tvUName = (TextView) findViewById(R.id.tv_set_uname);
        tvPhone = (TextView) findViewById(R.id.tv_set_phone);
        tvHouseId = (TextView) findViewById(R.id.tv_set_house_id);
        llPhone = (RelativeLayout) findViewById(R.id.ll_set_phone);
        llHouseId = (RelativeLayout) findViewById(R.id.ll_set_house_id);
        llUpdatePwd = (RelativeLayout) findViewById(R.id.ll_set_update_pwd);

        btnLogout = (CustomRippleButton) findViewById(R.id.btn_set_logout);

        winLoading = new LoadingWindow(
                LayoutInflater.from(this).inflate(R.layout.view_loading, null, false));

        updatePhoneView = LayoutInflater.from(this)
                .inflate(R.layout.view_center_change_phone, null, false);
        updatePwdView = LayoutInflater.from(this)
                .inflate(R.layout.view_center_update_pwd, null, false);
        if (updatePhoneView != null){
            tilPhone = (TextInputLayout) updatePhoneView.findViewById(R.id.til_center_phone);
            tilPhone.setHint(getString(R.string.tv_phone));
            mUpdatePhoneDialog = new SimpleDialog(this);
            mUpdatePhoneDialog.applyStyle(R.style.App_Dialog)
                    .title(getString(R.string.action_update_phone))
                    .positiveAction(R.string.action_update)
                    .negativeAction(R.string.action_back)
                    .contentView(updatePhoneView)
                    .cancelable(false)
                    .positiveActionClickListener(v -> onEditUserData(0, mUpdatePhoneDialog))
                    .negativeActionClickListener(v -> mUpdatePhoneDialog.dismiss());
        }
        if (updatePwdView != null){
            tilOldPwd = (TextInputLayout) updatePwdView.findViewById(R.id.til_center_opwd);
            tilNewPwd = (TextInputLayout) updatePwdView.findViewById(R.id.til_center_npwd);
            tilOldPwd.setHint(getString(R.string.til_hint_center_opwd));
            tilNewPwd.setHint(getString(R.string.til_hint_center_npwd));
            mUpdatePwdDialog = new SimpleDialog(this);
            mUpdatePwdDialog.applyStyle(R.style.App_Dialog)
                    .title(getString(R.string.action_update_pwd))
                    .positiveAction(R.string.action_update)
                    .negativeAction(R.string.action_back)
                    .contentView(updatePwdView)
                    .cancelable(false)
                    .positiveActionClickListener(v -> onEditUserData(1, mUpdatePwdDialog))
                    .negativeActionClickListener(v -> mUpdatePwdDialog.dismiss());
        }

        llPhone.setOnClickListener(v -> {
            if (mUpdatePhoneDialog != null) {
                mUpdatePhoneDialog.show();
            }
        });
        llHouseId.setOnClickListener(v ->
            IntentUtils.startPreviewActivity(
                    CenterActivity.this, new Intent(CenterActivity.this, HouseViewActivity.class))
        );
        llUpdatePwd.setOnClickListener(v -> {
            if (mUpdatePwdDialog != null) {
                mUpdatePwdDialog.show();
            }
        });
        btnLogout.setOnClickListener(v -> onLogOut());
        // read cache
        try{
            List<User> users =
                    DatabaseHelper.getDatabaseHelper(this).getDao(User.class).queryForAll();
            if (users != null && users.size() > 0){
                String uname = PreferencesUtils.getString(this, Config.KEY_OF_USER_NAME);
                if (uname == null || TextUtils.isEmpty(uname)){
                    return;
                }
                for (User user : users) {
                    if (user.getUsername().equals(uname)){
                        onShowData(user);
                    }
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        // request data
        onRequestData();
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 请求用户信息
     */
    private void onRequestData(){
        if (NetWorkUtils.getNetWorkType(this) == NetWorkUtils.NETWORK_TYPE_DISCONNECT){
            SystemUtil.showToast(R.string.msg_network_disconnect);
            return;
        }
        String validate = PreferencesUtils.getString(this, Config.KEY_OF_VALIDATE);
        Map<String, String> map = new HashMap<String, String>();
        map.put("validate", validate);
        SignCarrier signCarrier = SignatureUtil.getSignature(map);
        GlApplication.getApiService().onGetUserData(
                    signCarrier.getAppId(), signCarrier.getNonce(), signCarrier.getTimestamp(),
                    signCarrier.getSignature(), validate)
                .observeOn(AndroidSchedulers.mainThread())
                .map(userBaseJson -> userBaseJson.getData())
                .subscribe(user -> onCacheData(user), Throwable::printStackTrace);
//        .subscribe(new BaseSubscriber<BaseJson<User>>() {
//            @Override
//            public void onError(Throwable e) {
//                super.onError(e);
//            }
//
//            @Override
//            public void onNext(BaseJson<User> userBaseJson) {
//                super.onNext(userBaseJson);
//                User user;
//                if ((user = userBaseJson.getData()) == null) {
//                    SystemUtil.showToast(R.string.msg_nullpointer_error);
//                    return;
//                }
//                onCacheData(user);
//            }
//        });
    }

    /**
     * 缓存用户信息
     * @param user
     */
    private void onCacheData(User user){
        if (user == null){
            return;
        }
        try{
            Dao userDao = DatabaseHelper.getDatabaseHelper(this).getDao(User.class);
            userDao.update(user);
            onShowData(user);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * 展示数据
     * @param user
     */
    private void onShowData(User user){
        if (user == null){
            SystemUtil.showToast(R.string.msg_user_null);
            return;
        }
        tvUName.setText(getString(R.string.tv_set_uname) + user.getUsername());
        tvPhone.setText(user.getPhone() == null ? "" : user.getPhone());
        tvHouseId.setText(Long.toString(user.getHouseid()));
        Picasso.with(this)
                .load(user.getAvatar() == null ? "" : user.getAvatar())
                .placeholder(R.drawable.ic_timer_auto_grey600_48dp)
                .into(ivAvatar);
    }

    /**
     * logout
     */
    private void onLogOut(){
        try{
            // delete bill table, image table, house table, user table
            Dao billDao = DatabaseHelper.getDatabaseHelper(this).getDao(Bill.class);
            Dao imageDao = DatabaseHelper.getDatabaseHelper(this).getDao(Image.class);
            Dao houseDao = DatabaseHelper.getDatabaseHelper(this).getDao(House.class);
            Dao userDao = DatabaseHelper.getDatabaseHelper(this).getDao(User.class);

            List<Bill> bills = billDao.queryForAll();
            for (Bill bill : bills) {
                List<Image> images = imageDao.queryForEq("bid", bill.getBid());
                imageDao.delete(images);
            }
            billDao.delete(bills);
            List<House> houses = houseDao.queryForAll();
            for (House house : houses) {
                List<User> users = userDao.queryForEq("houseid", house.getHid());
                userDao.delete(users);
            }
            houseDao.delete(houses);

            // check and delete again
            List<Image> images = imageDao.queryForAll();
            if (!images.isEmpty()) {
                imageDao.delete(images);
            }
            List<User> users = userDao.queryForAll();
            if (!users.isEmpty()) {
                userDao.delete(users);
            }

            PreferencesUtils.putBoolean(this, Config.KEY_OF_LOGIN_FLAG, false);
            PreferencesUtils.putInt(this, Config.KEY_OF_USER_TYPE, -1);
            PreferencesUtils.putString(this, Config.KEY_OF_USER_NAME, "");
            PreferencesUtils.putString(this, Config.KEY_OF_VALIDATE, "");

            sendBroadcast(new Intent(Config.ACTION_LOGOUT));
            startActivity(new Intent(this, SplashActivity.class));
            finish();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * edit user data
     * @param type
     */
    private void onEditUserData(int type, Dialog dialog){
        if (NetWorkUtils.getNetWorkType(this) == NetWorkUtils.NETWORK_TYPE_DISCONNECT){
            SystemUtil.showToast(R.string.msg_network_disconnect);
            return;
        }
        String value = "";
        String value1 = "";

        if (type == 0){
            value = tilPhone.getEditText().getText().toString();
            if (TextUtils.isEmpty(value)){
                SystemUtil.showToast(R.string.msg_all_not_empty);
                return;
            }
        }else if (type == 1){
            try {
                value1 = MD5.getMD5(tilOldPwd.getEditText().getText().toString());
                value = MD5.getMD5(tilNewPwd.getEditText().getText().toString());
            }catch (NoSuchAlgorithmException e){
                e.printStackTrace();
            }
            if (TextUtils.isEmpty(value1) || TextUtils.isEmpty(value)){
                SystemUtil.showToast(R.string.msg_all_not_empty);
                return;
            }
        }
        onShowAndHide(winLoading, true, dialog, false);

        String validate = PreferencesUtils.getString(this, Config.KEY_OF_VALIDATE);
        Map<String, String> map = new HashMap<String, String>();
        map.put("type", Integer.toString(type));
        map.put("value", value);
        map.put("value1", value1);
        map.put("validate", validate);
        SignCarrier signCarrier = SignatureUtil.getSignature(map);
        GlApplication.getApiService().onEditUserData(
                signCarrier.getAppId(), signCarrier.getNonce(), signCarrier.getTimestamp(),
                signCarrier.getSignature(), Integer.toString(type), value, value1, validate)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new BaseSubscriber<BaseJson<User>>() {
                @Override public void onError(Throwable e) {
                    onShowAndHide(winLoading, false, dialog, true);
                    super.onError(e);
                }

                @Override public void onNext(BaseJson<User> userBaseJson) {
//                    super.onNext(userBaseJson);
                    if (null == userBaseJson) {
                        onShowAndHide(winLoading, false, dialog, true);
                        SystemUtil.showToast(R.string.msg_nullpointer_error);
                        return;
                    }
                    if (userBaseJson.getValidate() != null
                            && !TextUtils.isEmpty(userBaseJson.getValidate())) {
                        PreferencesUtils.putString(CenterActivity.this,
                                Config.KEY_OF_VALIDATE, userBaseJson.getValidate());
                    }
                    // not Correct status
                    if (userBaseJson.getStatus() != Config.STATUS_CODE_OK) {
                        onShowAndHide(winLoading, false, dialog, true);
                        SystemUtil.showToast(userBaseJson.getMessage());
                        return;
                    }
                    SystemUtil.showToast(userBaseJson.getMessage());
                    onRequestData();
                    onShowAndHide(winLoading, false, dialog, false);
                }
            });
    }

    /**
     * show or hide LoadingWindow & Dialog
     * @param winLoading
     * @param isShow1
     * @param dialog
     * @param isShow2
     */
    private void onShowAndHide(
            LoadingWindow winLoading, boolean isShow1, Dialog dialog, boolean isShow2){
        if (isShow1){
            if (!winLoading.isShowing()) {
                winLoading.showAsDropDown(mToolbar, 0, 0);
            }
        }else {
            if (winLoading.isShowing()){
                winLoading.dismiss();
            }
        }
        if (isShow2){
            if (!dialog.isShowing()){
                dialog.show();
                SystemUtil.showOrHideInputMethodManager(this);
            }
        }else {
            if (dialog.isShowing()){
                dialog.hide();
                SystemUtil.showOrHideInputMethodManager(this);
            }
        }
    }
}