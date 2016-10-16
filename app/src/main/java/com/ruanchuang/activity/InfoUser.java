package com.ruanchuang.activity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ruanchuang.activity.designlibrary.R;
import com.runachuang.massorganizationsignin.utils.ConstantValue;
import com.runachuang.massorganizationsignin.utils.MyUser;
import com.runachuang.massorganizationsignin.utils.SpUtils;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2016/9/10/010.
 */
public class InfoUser extends AppCompatActivity implements View.OnClickListener {
    private String mName;
    private String mSex;
    private String mGroup;
    private String mQq;
    private String mPhone;
    private SharedPreferences sp;
    private AlertDialog dialog;
    private View mView;
    private TextView tv_name;
    private TextView tv_sex;
    private TextView tv_group;
    private TextView tv_phone;
    private TextView tv_qq;
    private String[] mGroupDes;
    private String[] mSexDes;
    private int mGroupIndex1;
    private int mSexIndex;
    private TextView tv_quse;
    private String mQues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infouser);

        sp = getSharedPreferences("user", MODE_PRIVATE);
        mName = sp.getString("name", "");
        mSex = sp.getString("sex", "");
        mGroup = sp.getString("group", "");
        mQq = sp.getString("qq", "");
        mPhone = sp.getString("phone", "");
        mQues = sp.getString("ques", "");

        mGroupDes = new String[]{"行政", "PHP组", "视频组", "Android组", "Java组",".NET组","美工组"};
        mSexDes = new String[]{"男", "女"};

        initDialog();

        initUI();

        initBt();
    }

    private int findIndex(String group) {
        int k=0,i=0;
        for (String mGroupDe : mGroupDes) {
            if(mGroupDe.equals(group)){
                k = i;
            }
            i++;
        }
        return k;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mName = sp.getString("name", "");
        mSex = sp.getString("sex", "");
        mGroup = sp.getString("group", "");
        mQq = sp.getString("qq", "");
        mPhone = sp.getString("phone", "");
        mQues = sp.getString("ques", "");

        tv_name.setText("姓名：" + mName);
        tv_sex.setText("性别：" + mSex);
        tv_group.setText("组别：" + mGroup);
        tv_phone.setText("电话：" + mPhone);
        tv_qq.setText("QQ：" + mQq);
        tv_quse.setText("软创在哪：" + mQues);
    }

    private void initDialog() {
        // 自定义对话框 要用dialog.setView(mView); 来设置
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialog = builder.create();

        // 将一个xml转换成一个view对象
        mView = View.inflate(this, R.layout.dialog_view, null);
        // 兼容低版本 去掉对话框的内边距
        dialog.setView(mView, 0, 0, 0, 0);

    }

    protected void showSingleChoiceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择组别");
        mGroupIndex1 = SpUtils.getIntSp(getApplicationContext(), ConstantValue.GROUP_NAME_INT, findIndex(mGroup));
        // 设置单选框(选项内容, 被选中的条目索引, 监听事件)
        builder.setSingleChoiceItems(mGroupDes, mGroupIndex1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, final int which) {
                //关闭对话框
                dialog.dismiss();

                MyUser newUser = new MyUser();
                newUser.setGroup(mGroupDes[which]);
                BmobUser bmobUser = BmobUser.getCurrentUser(MyUser.class);
                newUser.update(bmobUser.getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            Toast.makeText(getApplicationContext(), "更新用户信息成功", Toast.LENGTH_SHORT).show();
                            SharedPreferences.Editor edit = sp.edit();
                            edit.putString("group", mGroupDes[which]);
                            edit.commit();
                            tv_group.setText("组别：" + mGroupDes[which]);
                            //记录点击的索引值
                            SpUtils.putIntSp(getApplicationContext(), ConstantValue.GROUP_NAME_INT, which);
                        } else {
                            Toast.makeText(getApplicationContext(), "更新用户信息失败"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        // 设置消极的按钮
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        // 显示对话框
        builder.show();
    }

    protected void showChoiceSexDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择性别");
        mSexIndex = SpUtils.getIntSp(getApplicationContext(), ConstantValue.SEX_INT, 0);
        // 设置单选框(选项内容, 被选中的条目索引, 监听事件)
        builder.setSingleChoiceItems(mSexDes, mSexIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, final int which) {
                //关闭对话框
                dialog.dismiss();

                MyUser newUser = new MyUser();
                newUser.setSex(mSexDes[which]);
                BmobUser bmobUser = BmobUser.getCurrentUser(MyUser.class);
                newUser.update(bmobUser.getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            Toast.makeText(getApplicationContext(), "更新用户信息成功", Toast.LENGTH_SHORT).show();
                            SharedPreferences.Editor edit = sp.edit();
                            edit.putString("sex", mSexDes[which]);
                            edit.commit();
                            tv_sex.setText("性别：" + mSexDes[which]);
                            //记录点击的索引值
                            SpUtils.putIntSp(getApplicationContext(), ConstantValue.SEX_INT, which);
                        } else {
                            Toast.makeText(getApplicationContext(), "更新用户信息失败"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        // 设置消极的按钮
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        // 显示对话框
        builder.show();
    }

    private void initBt() {
        Button bt_name = (Button) findViewById(R.id.bt_name);
        Button bt_sex = (Button) findViewById(R.id.bt_sex);
        Button bt_group = (Button) findViewById(R.id.bt_group);
        Button bt_phone = (Button) findViewById(R.id.bt_phone);
        Button bt_qq = (Button) findViewById(R.id.bt_qq);
        Button bt_quse = (Button) findViewById(R.id.bt_quse);

        bt_name.setOnClickListener(this);
        bt_sex.setOnClickListener(this);
        bt_group.setOnClickListener(this);
        bt_phone.setOnClickListener(this);
        bt_qq.setOnClickListener(this);
        bt_quse.setOnClickListener(this);
    }

    private void initUI() {
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_sex = (TextView) findViewById(R.id.tv_sex);
        tv_group = (TextView) findViewById(R.id.tv_group);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_qq = (TextView) findViewById(R.id.tv_qq);
        tv_quse = (TextView) findViewById(R.id.tv_quse);

        tv_name.setText("姓名：" + mName);
        tv_sex.setText("性别：" + mSex);
        tv_group.setText("组别：" + mGroup);
        tv_phone.setText("电话：" + mPhone);
        tv_qq.setText("QQ：" + mQq);
        tv_quse.setText("软创在哪：" + mQues);
    }

    @Override
    public void onClick(View view) {
        TextView tv_title = (TextView) mView.findViewById(R.id.tv_title);
        final EditText tv_in = (EditText) mView.findViewById(R.id.tv_in);
        Button bt_submit = (Button) mView.findViewById(R.id.bt_submit);
        Button bt_cancel = (Button) mView.findViewById(R.id.bt_cancel);

        switch (view.getId()) {
            case R.id.bt_name:
                tv_title.setText("修改姓名");
                tv_in.setHint("请输入姓名");
                bt_submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String content = tv_in.getText().toString().trim();
                        // 如果内容不为空
                        if (!TextUtils.isEmpty(content)) {
                            MyUser newUser = new MyUser();
                            newUser.setName(content);
                            BmobUser bmobUser = BmobUser.getCurrentUser(MyUser.class);
                            newUser.update(bmobUser.getObjectId(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        Toast.makeText(getApplicationContext(), "更新用户信息成功", Toast.LENGTH_SHORT).show();
                                        SharedPreferences.Editor edit = sp.edit();
                                        edit.putString("name", content);
                                        edit.commit();
                                        tv_name.setText("姓名：" + content);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "更新用户信息失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            // 隐藏对话框
                            dialog.dismiss();
                            tv_in.setText("");
                        } else {
                            Toast.makeText(getApplicationContext(), "输入的内容不能为空", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                bt_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        tv_in.setText("");
                    }
                });
                dialog.show();
                break;
            case R.id.bt_sex:
                // 展示单选对话框
                showChoiceSexDialog();
                break;
            case R.id.bt_group:
                // 展示单选对话框
                showSingleChoiceDialog();
                break;
            case R.id.bt_phone:
                tv_title.setText("修改电话");
                tv_in.setHint("请输入电话号码");
                bt_submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String content = tv_in.getText().toString().trim();
                        // 如果内容不为空
                        if (!TextUtils.isEmpty(content)) {
                            MyUser newUser = new MyUser();
                            newUser.setMobilePhoneNumber(content);
                            BmobUser bmobUser = BmobUser.getCurrentUser(MyUser.class);
                            newUser.update(bmobUser.getObjectId(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        Toast.makeText(getApplicationContext(), "更新用户信息成功", Toast.LENGTH_SHORT).show();
                                        SharedPreferences.Editor edit = sp.edit();
                                        edit.putString("phone", content);
                                        edit.commit();
                                        tv_phone.setText("电话：" + content);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "手机号码格式错误，或该号码已被注册", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            // 隐藏对话框
                            dialog.dismiss();
                            tv_in.setText("");
                        } else {
                            Toast.makeText(getApplicationContext(), "输入的内容不能为空", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                bt_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        tv_in.setText("");
                    }
                });
                dialog.show();
                break;
            case R.id.bt_qq:
                tv_title.setText("修改QQ");
                tv_in.setHint("请输入QQ号码");
                bt_submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String content = tv_in.getText().toString().trim();
                        // 如果内容不为空
                        if (!TextUtils.isEmpty(content)) {
                            MyUser newUser = new MyUser();
                            newUser.setQq(content);
                            BmobUser bmobUser = BmobUser.getCurrentUser(MyUser.class);
                            newUser.update(bmobUser.getObjectId(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        Toast.makeText(getApplicationContext(), "更新用户信息成功", Toast.LENGTH_SHORT).show();
                                        SharedPreferences.Editor edit = sp.edit();
                                        edit.putString("qq", content);
                                        edit.commit();
                                        tv_qq.setText("QQ：" + content);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "更新用户信息失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            // 隐藏对话框
                            dialog.dismiss();
                            tv_in.setText("");
                        } else {
                            Toast.makeText(getApplicationContext(), "输入的内容不能为空", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                bt_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        tv_in.setText("");
                    }
                });
                dialog.show();
                break;
            case R.id.bt_quse:
                tv_title.setText("修改问题答案");
                tv_in.setHint("软创在哪里");
                bt_submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String content = tv_in.getText().toString().trim();
                        // 如果内容不为空
                        if (!TextUtils.isEmpty(content)) {
                            MyUser newUser = new MyUser();
                            newUser.setQues(content);
                            BmobUser bmobUser = BmobUser.getCurrentUser(MyUser.class);
                            newUser.update(bmobUser.getObjectId(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        Toast.makeText(getApplicationContext(), "更新问题成功", Toast.LENGTH_SHORT).show();
                                        SharedPreferences.Editor edit = sp.edit();
                                        edit.putString("ques", content);
                                        edit.commit();
                                        tv_quse.setText("软创在哪里：" + content);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "更新问题失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            // 隐藏对话框
                            dialog.dismiss();
                            tv_in.setText("");
                        } else {
                            Toast.makeText(getApplicationContext(), "输入的内容不能为空", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                bt_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        tv_in.setText("");
                    }
                });
                dialog.show();
                break;
        }
    }

}
