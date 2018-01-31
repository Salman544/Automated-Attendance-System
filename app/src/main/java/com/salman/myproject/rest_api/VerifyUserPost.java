package com.salman.myproject.rest_api;

import java.util.List;

/**
 * Created by Salman on 1/1/2018.
 */

public class VerifyUserPost {


    private List<ImagesBean> images;

    public List<ImagesBean> getImages() {
        return images;
    }

    public void setImages(List<ImagesBean> images) {
        this.images = images;
    }

    public static class ImagesBean {
        /**
         * transaction : {"status":"success","subject_id":"Elizabeth","quality":0.84705,"width":170,"height":287,"topLeftX":108,"topLeftY":55,"confidence":0.88309,"gallery_name":"MyGallery"}
         */

        private TransactionBean transaction;

        public TransactionBean getTransaction() {
            return transaction;
        }

        public void setTransaction(TransactionBean transaction) {
            this.transaction = transaction;
        }

        public static class TransactionBean {
            /**
             * status : success
             * subject_id : Elizabeth
             * quality : 0.84705
             * width : 170
             * height : 287
             * topLeftX : 108
             * topLeftY : 55
             * confidence : 0.88309
             * gallery_name : MyGallery
             */

            private String status;
            private String subject_id;
            private double quality;
            private int width;
            private int height;
            private int topLeftX;
            private int topLeftY;
            private double confidence;
            private String gallery_name;

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getSubject_id() {
                return subject_id;
            }

            public void setSubject_id(String subject_id) {
                this.subject_id = subject_id;
            }

            public double getQuality() {
                return quality;
            }

            public void setQuality(double quality) {
                this.quality = quality;
            }

            public int getWidth() {
                return width;
            }

            public void setWidth(int width) {
                this.width = width;
            }

            public int getHeight() {
                return height;
            }

            public void setHeight(int height) {
                this.height = height;
            }

            public int getTopLeftX() {
                return topLeftX;
            }

            public void setTopLeftX(int topLeftX) {
                this.topLeftX = topLeftX;
            }

            public int getTopLeftY() {
                return topLeftY;
            }

            public void setTopLeftY(int topLeftY) {
                this.topLeftY = topLeftY;
            }

            public double getConfidence() {
                return confidence;
            }

            public void setConfidence(double confidence) {
                this.confidence = confidence;
            }

            public String getGallery_name() {
                return gallery_name;
            }

            public void setGallery_name(String gallery_name) {
                this.gallery_name = gallery_name;
            }
        }
    }
}
