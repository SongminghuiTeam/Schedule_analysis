package com.cloudea.basemodule;

import java.util.Date;

/**
 * 当上传云端成功后会填充object_id
 * 当从云端查询后也会填充object_id
 * 当从本地数据库查询后会填充id
 */
public class Data {
    public int id;              //本地标识
    public String object_id;    //云端标识
    public Date start_time;
    public Date end_time;
    public String addr;
    public boolean state;
    public String content;
    public int note_time;
}
