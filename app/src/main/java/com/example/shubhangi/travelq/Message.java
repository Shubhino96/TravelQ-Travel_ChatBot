package com.example.shubhangi.travelq;

/**
 * Created by shubhangi on 16-04-2018.
 */

public class Message {
    String msg;
    String usr_type;

    Message(){

    }

    Message(String msg,String user){
        this.msg=msg;
        usr_type=user;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getUsr_type() {
        return usr_type;
    }

    public void setUsr_type(String usr_type) {
        this.usr_type = usr_type;
    }


}
