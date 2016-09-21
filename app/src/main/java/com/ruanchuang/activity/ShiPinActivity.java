package com.ruanchuang.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ruanchuang.activity.designlibrary.R;

import net.steamcrafted.loadtoast.LoadToast;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;

/**
 * Created by joho on 2016/5/26.
 */
public class ShiPinActivity extends AppCompatActivity {
    private List<Person> lists;
    private ListView lv_shipin;
    private static final String TAG = "ShiPinActivity";
    private LoadToast lt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipin);
        lt = new LoadToast(this);
        lv_shipin = (ListView) findViewById(R.id.lv_shipin);
        lists = new ArrayList<Person>();
        lt.setText("加载数据中...").setTranslationY(100).show();

        //读取服务器数据
        ReadUtlis.queryData("视频组", lists, new ReadUtlis.CallBack() {
            @Override
            public void success() {
                lt.success();
                lv_shipin.setAdapter(new MyAdapter());
            }

            @Override
            public void error() {
                lt.error();
                Toast.makeText(getApplicationContext(), "加载失败，请重新加载", Toast.LENGTH_SHORT).show();
            }
        });

        //设置listview条目的点击事件
        lv_shipin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Person p = lists.get(i);
                BmobIMUserInfo info = new BmobIMUserInfo(p.getId(), p.getGroup()+" "+p.getName(), "");
                //启动一个会话，实际上就是在本地数据库的会话列表中先创建（如果没有）与该用户的会话信息，且将用户信息存储到本地的用户表中
                BmobIMConversation c = BmobIM.getInstance().startPrivateConversation(info, null);
                Bundle bundle = new Bundle();
                bundle.putSerializable("c", c);
                bundle.putString("title",p.getGroup()+" "+p.getName());
                Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
                intent.putExtra("bundle",bundle);
                startActivity(intent);
            }
        });
        lv_shipin.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView tv_phone = (TextView) view.findViewById(R.id.tv_phone);
                TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
                String phone = tv_phone.getText().toString().trim();
                String name = tv_name.getText().toString().trim();
                //将文字复制到系统粘贴板
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData data = ClipData.newPlainText("text",phone);
                cm.setPrimaryClip(data);
                Toast.makeText(getApplicationContext(), "已将"+name+"的电话复制到粘贴板", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    //定义listview的数据适配器
    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return lists.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                view = View.inflate(getApplicationContext(), R.layout.item, null);
            } else {
                view = convertView;
            }

            TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
            TextView tv_phone = (TextView) view.findViewById(R.id.tv_phone);
            TextView tv_group = (TextView) view.findViewById(R.id.tv_group);
            ImageView iv_sex = (ImageView) view.findViewById(R.id.iv_sex);

            Person person = lists.get(position);
            String sex = person.getSex();
            if ("男".equals(sex)) {
                iv_sex.setImageResource(R.drawable.icon_male);
            }
            if ("女".equals(sex)) {
                iv_sex.setImageResource(R.drawable.icon_female);
            }
            tv_name.setText(person.getName());
            tv_phone.setText(person.getPhone());
            tv_group.setText(person.getGroup());

            return view;
        }

    }
}
