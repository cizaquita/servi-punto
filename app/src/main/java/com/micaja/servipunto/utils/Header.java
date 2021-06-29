package com.micaja.servipunto.utils;

import java.util.Hashtable;

import org.ksoap2.serialization.PropertyInfo;

/**
 * Created by fabioarias on 28/04/15.
 */
public class Header implements org.ksoap2.serialization.KvmSerializable {
    private String version = "V1_1";
    private String user = null;
    private String password = null;
    private String token = null;
    private String serial = null;
    private String sign = null;
    private String trace = null;
    private String localdate = null;
    private String processid = null;

    @Override
    public Object getProperty(int arg0)
    {
        switch(arg0)
        {
            case 0:
                return version;
            case 1:
                return user;
            case 2:
                return password;
            case 3:
                return token;
            case 4:
                return serial;
            case 5:
                return sign;
            case 6:
                return trace;
            case 7:
                return localdate;
            case 8:
                return processid;
        }
        return null;
    }

    @Override
    public int getPropertyCount()
    {
        return 9;
    }

    @Override
    public void getPropertyInfo(int arg0, Hashtable arg1, PropertyInfo arg2)
    {
        switch(arg0)
        {
            case 0:
                arg2.type = PropertyInfo.STRING_CLASS;
                arg2.name = "version";
                break;
            case 1:
                arg2.type = PropertyInfo.STRING_CLASS;
                arg2.name = "user";
                break;
            case 2:
                arg2.type = PropertyInfo.STRING_CLASS;
                arg2.name = "password";
                break;
            case 3:
                arg2.type = PropertyInfo.STRING_CLASS;
                arg2.name = "token";
                break;
            case 4:
                arg2.type = PropertyInfo.STRING_CLASS;
                arg2.name = "serial";
                break;
            case 5:
                arg2.type = PropertyInfo.STRING_CLASS;
                arg2.name = "sign";
                break;
            case 6:
                arg2.type = PropertyInfo.STRING_CLASS;
                arg2.name = "trace";
                break;
            case 7:
                arg2.type = PropertyInfo.STRING_CLASS;
                arg2.name = "localdate";
                break;
            case 8:
                arg2.type = PropertyInfo.STRING_CLASS;
                arg2.name = "processid";
                break;
            default:break;
        }

    }

    @Override
    public void setProperty(int arg0, Object arg1)
    {
        switch(arg0)
        {
            case 0:
                version = arg1.toString();
                break;
            case 1:
                user = arg1.toString();
                break;
            case 2:
                password = arg1.toString();
                break;
            case 3:
                token = arg1.toString();
                break;
            case 4:
                serial = arg1.toString();
                break;
            case 5:
                sign = arg1.toString();
                break;
            case 6:
                trace = arg1.toString();
                break;
            case 7:
                localdate = arg1.toString();
                break;
            case 8:
                processid = arg1.toString();
                break;
            default:
                break;
        }

    }


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getTrace() {
        return trace;
    }

    public void setTrace(String trace) {
        this.trace = trace;
    }

    public String getLocaldate() {
        return localdate;
    }

    public void setLocaldate(String localdate) {
        this.localdate = localdate;
    }

    public String getProcessid() {
        return processid;
    }

    public void setProcessid(String processid) {
        this.processid = processid;
    }
}
