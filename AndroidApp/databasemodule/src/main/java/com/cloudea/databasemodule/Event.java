package com.cloudea.databasemodule;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

public class Event extends BmobObject {
    public User user_id;
    public Message message_id;
    public String content;
    public BmobDate start_time;
    public BmobDate end_time;
    public String addr;
    public Integer note_time;
    public Boolean state;
}
