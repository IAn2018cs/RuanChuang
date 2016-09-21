package com.ruanchuang.activity;

import android.util.Log;

import com.runachuang.massorganizationsignin.utils.MyUser;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by joho on 2016/5/29.
 */
public class ReadUtlis {
    public static void queryData(String lvgroup, final List<Person> lists,final CallBack cb){
        final MyUser user = BmobUser.getCurrentUser(MyUser.class);

        BmobQuery<MyUser> query = new BmobQuery<MyUser>();
        //查询playerName叫“比目”的数据
        query.addWhereEqualTo("group", lvgroup);
        //返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(50);
        //执行查询方法
        query.findObjects(new FindListener<MyUser>() {
            @Override
            public void done(List<MyUser> object, BmobException e) {
                if(e==null){
                    for (MyUser myUser : object) {
                        if(!user.getObjectId().equals(myUser.getObjectId())){
                            //把数据封装的javabean
                            Person person = new Person();
                            person.setId(myUser.getObjectId());
                            person.setName(myUser.getName());
                            person.setSex(myUser.getSex());
                            person.setGroup(myUser.getGroup());
                            person.setPhone(myUser.getMobilePhoneNumber());
                            //把javabean对象加入到集合
                            lists.add(person);
                        }
                    }
                    cb.success();
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                    cb.error();
                }
            }
        });
    }

    public interface CallBack{
        public abstract void success();
        public abstract void error();
    }
}

