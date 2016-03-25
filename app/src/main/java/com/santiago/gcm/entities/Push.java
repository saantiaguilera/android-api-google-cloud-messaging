package com.santiago.gcm.entities;

import android.os.Bundle;

import com.santiago.entity.JSONEntity;

import org.json.JSONException;
import org.json.JSONObject;

public class Push extends JSONEntity {

    /*-------------------------------------------------JSON Fields References-------------------------------------------------*/

    public static final String FROM_JSON = "from";
    public static final String TITLE_JSON = "title";
    public static final String MESSAGE_JSON = "message";

    /*-------------------------------------------------Fields-----------------------------------------------------------------*/

    private String from;
    private String title;
    private String message;

    /*------------------------------------------------Constructors------------------------------------------------------------*/

    public Push() {
        super();
    }

    public Push(Push entity) {
        super(entity);
    }

    public Push(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    public Push(String json) throws JSONException {
        this(new JSONObject(json));
    }

    public Push(String from,Bundle data){
        setFrom(from);
        setValuesFrom(data);
    }

    /*------------------------------------------------Getters------------------------------------------------------------*/

    public String getFrom() {
        return from;
    }

    public String getMessage() {
        return message;
    }

    public String getTitle() {
        return title;
    }

    /*------------------------------------------------Setters------------------------------------------------------------*/

    public void setFrom(String from) {
        this.from = from;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setValuesFrom(Push push){
        super.setValuesFrom(push);

        if(push != null) {
            setFrom(push.getFrom());
            setMessage(push.getMessage());
            setTitle(push.getTitle());
        } else setDefaultValues();
    }

    public void setValuesFrom(Bundle data) {
        setMessage(data.getString(MESSAGE_JSON));
        setTitle(data.getString(TITLE_JSON));
    }

    @Override
    public void setValuesFrom(JSONObject jsonObject) throws JSONException {
        super.setValuesFrom(jsonObject);

        setFrom(jsonObject.optString(FROM_JSON));
        setMessage(jsonObject.optString(MESSAGE_JSON));
        setTitle(jsonObject.optString(TITLE_JSON));
    }

    @Override
    public void setDefaultValues() {
        super.setDefaultValues();

        setFrom(null);
        setMessage(null);
        setTitle(null);
    }

    /*----------------------------------------------------JSON Serializer---------------------------------------------------------*/

    @Override
    public JSONObject asJSONObject() {
        JSONObject jsonObject = super.asJSONObject();

        try {
            jsonObject.put(FROM_JSON,getFrom());
            jsonObject.put(MESSAGE_JSON, getMessage());
            jsonObject.put(TITLE_JSON,getTitle());
        } catch (JSONException e){}

        return jsonObject;
    }

}
