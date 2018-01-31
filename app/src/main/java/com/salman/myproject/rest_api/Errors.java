package com.salman.myproject.rest_api;

import java.util.List;

/**
 * Created by Salman on 1/1/2018.
 */

public class Errors {


    private List<ErrorsBean> Errors;

    public List<ErrorsBean> getErrors() {
        return Errors;
    }

    public void setErrors(List<ErrorsBean> Errors) {
        this.Errors = Errors;
    }

    public static class ErrorsBean {
        /**
         * Message : no faces found in the image
         * ErrCode : 5002
         */

        private String Message;
        private int ErrCode;

        public String getMessage() {
            return Message;
        }

        public void setMessage(String Message) {
            this.Message = Message;
        }

        public int getErrCode() {
            return ErrCode;
        }

        public void setErrCode(int ErrCode) {
            this.ErrCode = ErrCode;
        }
    }
}
