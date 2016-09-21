package com.ruanchuang.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ruanchuang.activity.designlibrary.R;
import com.runachuang.massorganizationsignin.utils.ChatAdapter;

import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMTextMessage;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.listener.MessageListHandler;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.newim.listener.MessagesQueryListener;
import cn.bmob.newim.listener.ObseverListener;
import cn.bmob.newim.notification.BmobNotificationManager;
import cn.bmob.v3.exception.BmobException;

/**
 * Created by Administrator on 2016/9/15/015.
 */
public class ChatActivity extends AppCompatActivity implements ObseverListener,MessageListHandler {
    BmobIMConversation c;
    private EditText et;
    private Button bt;
    private SwipeRefreshLayout sw_refresh;
    private LinearLayout ll_chat;
    ChatAdapter adapter;
    protected LinearLayoutManager layoutManager;
    private RecyclerView rc_view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        TextView tv = (TextView) findViewById(R.id.tv_name);
        initUI();

        c= BmobIMConversation.obtain(BmobIMClient.getInstance(), (BmobIMConversation) getBundle().getSerializable("c"));
        tv.setText(getBundle().getString("title",""));

        initSwipeLayout();

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = et.getText().toString();
                if(text.equals("")){
                    Toast.makeText(getApplicationContext(),"消息不能为空",Toast.LENGTH_SHORT).show();
                }else{
                    BmobIMTextMessage msg =new BmobIMTextMessage();
                    msg.setContent(text);
                    c.sendMessage(msg,listener);
                }
            }
        });
    }

    /**
     * 消息发送监听器
     */
    public MessageSendListener listener =new MessageSendListener() {

        @Override
        public void onStart(BmobIMMessage msg) {
            super.onStart(msg);
            adapter.addMessage(msg);
            et.setText("");
            scrollToBottom();
        }

        @Override
        public void done(BmobIMMessage msg, BmobException e) {
            if (e == null) {
                adapter.notifyDataSetChanged();
                et.setText("");
                scrollToBottom();
                Toast.makeText(getApplicationContext(),"发送成功",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void initSwipeLayout(){
        if(sw_refresh!=null){
            sw_refresh.setEnabled(true);
            layoutManager = new LinearLayoutManager(this);
            rc_view.setLayoutManager(layoutManager);
            adapter = new ChatAdapter(c);
            rc_view.setAdapter(adapter);
            ll_chat.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    ll_chat.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    sw_refresh.setRefreshing(true);
                    //自动刷新
                    queryMessages(null);
                }
            });
            //下拉加载
            sw_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    BmobIMMessage msg = adapter.getFirstMessage();
                    queryMessages(msg);
                }
            });
        }


    }

    /**首次加载，可设置msg为null，下拉刷新的时候，默认取消息表的第一个msg作为刷新的起始时间点，默认按照消息时间的降序排列
     * @param msg
     */
    public void queryMessages(BmobIMMessage msg){
        c.queryMessages(msg, 10, new MessagesQueryListener() {
            @Override
            public void done(List<BmobIMMessage> list, BmobException e) {
                sw_refresh.setRefreshing(false);
                if (e == null) {
                    if (null != list && list.size() > 0) {
                        adapter.addMessages(list);
                        layoutManager.scrollToPositionWithOffset(list.size() - 1, 0);
                    }
                } else {
                    Toast.makeText(getApplicationContext(),e.getMessage() + "(" + e.getErrorCode() + ")",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void scrollToBottom() {
        layoutManager.scrollToPositionWithOffset(adapter.getItemCount() - 1, 0);
    }

    private void initUI() {
        et = (EditText) findViewById(R.id.edit_msg);
        bt = (Button) findViewById(R.id.btn_chat_send);
        sw_refresh = (SwipeRefreshLayout) findViewById(R.id.sw_refresh);
        ll_chat = (LinearLayout) findViewById(R.id.ll_chat);
        rc_view = (RecyclerView) findViewById(R.id.rc_view);
    }

    public Bundle getBundle() {
        if (getIntent() != null)
            return getIntent().getBundleExtra("bundle");
        else
            return null;
    }

    @Override
    public void onMessageReceive(List<MessageEvent> list) {
        //Toast.makeText(getApplicationContext(),"聊天页面接收到消息"+ list.size(),Toast.LENGTH_SHORT).show();
        //当注册页面消息监听时候，有消息（包含离线消息）到来时会回调该方法
        for (int i=0;i<list.size();i++){
            addMessage2Chat(list.get(i));
        }
    }

    /**添加消息到聊天界面中
     * @param event
     */
    private void addMessage2Chat(MessageEvent event){
        BmobIMMessage msg =event.getMessage();
        if(c!=null && event!=null && c.getConversationId().equals(event.getConversation().getConversationId()) //如果是当前会话的消息
                && !msg.isTransient()){//并且不为暂态消息
            if(adapter.findPosition(msg)<0){//如果未添加到界面中
                adapter.addMessage(msg);
                //更新该会话下面的已读状态
                c.updateReceiveStatus(msg);
            }
            scrollToBottom();
        }else{
            Toast.makeText(getApplicationContext(),"不是与当前聊天对象的消息",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        //锁屏期间的收到的未读消息需要添加到聊天界面中
        addUnReadMessage();
        //添加页面消息监听器
        BmobIM.getInstance().addMessageListHandler(this);
        // 有可能锁屏期间，在聊天界面出现通知栏，这时候需要清除通知
        BmobNotificationManager.getInstance(this).cancelNotification();
        super.onResume();
    }

    /**
     * 添加未读的通知栏消息到聊天界面
     */
    private void addUnReadMessage(){
        List<MessageEvent> cache = BmobNotificationManager.getInstance(this).getNotificationCacheList();
        if(cache.size()>0){
            int size =cache.size();
            for(int i=0;i<size;i++){
                MessageEvent event = cache.get(i);
                addMessage2Chat(event);
            }
        }
        scrollToBottom();
    }

    @Override
    protected void onPause() {
        //移除页面消息监听器
        BmobIM.getInstance().removeMessageListHandler(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        //更新此会话的所有消息为已读状态
        if(c!=null){
            c.updateLocalCache();
        }
        super.onDestroy();
    }
}
