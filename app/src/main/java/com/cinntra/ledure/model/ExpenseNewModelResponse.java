package com.cinntra.ledure.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ExpenseNewModelResponse implements Serializable
{

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("data")
    @Expose
    private List<ExpenseNewDataModel> data = null;
    private final static long serialVersionUID = 299612448943767153L;

    /**
     * No args constructor for use in serialization
     *
     */
    public ExpenseNewModelResponse() {
    }

    /**
     *
     * @param data
     * @param message
     * @param status
     */
    public ExpenseNewModelResponse(String message, Integer status, List<ExpenseNewDataModel> data) {
        super();
        this.message = message;
        this.status = status;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<ExpenseNewDataModel> getData() {
        return data;
    }

    public void setData(List<ExpenseNewDataModel> data) {
        this.data = data;
    }

}
