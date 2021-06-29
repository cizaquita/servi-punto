package com.micaja.servipunto.utils;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

/**
 * Created by fabioarias on 28/04/15.
 */
public class Data implements KvmSerializable{

    private String data = null;
    private String type = null;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public Object getProperty(int index) {
        switch(index)
        {
            case 0:
                return data;
            case 1:
                return type;
        }
        return null;
    }

    @Override
    public int getPropertyCount() {
        return 2;
    }

    @Override
    public void setProperty(int index, Object value) {
        switch(index)
        {
            case 0:
                data = value.toString();
                break;
            case 1:
                type = value.toString();
                break;
        }
    }

    @Override
    public void getPropertyInfo(int index, Hashtable properties, PropertyInfo info) {
        switch(index) {
            case 0:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "data";
                break;
            case 1:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "type";
                break;
        }
    }
}
