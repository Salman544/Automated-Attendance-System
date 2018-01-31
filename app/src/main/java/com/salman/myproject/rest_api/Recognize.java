package com.salman.myproject.rest_api;

import java.util.List;

/**
 * Created by Salman on 1/1/2018.
 */

public class Recognize {


    private List<ImagesBean> images;

    public List<ImagesBean> getImages() {
        return images;
    }

    public void setImages(List<ImagesBean> images) {
        this.images = images;
    }

    public static class ImagesBean {
        /**
         * transaction : {"status":"success","width":175,"topLeftX":103,"topLeftY":157,"gallery_name":"MyGallery","face_id":1,"confidence":0.86944,"subject_id":"Elizabeth","height":175,"quality":0.84705}
         * candidates : [{"subject_id":"Elizabeth","confidence":0.86944,"face_id":"5a48b0c02ecdf7613802","enrollment_timestamp":"1486925605094"}]
         */

        private TransactionBean transaction;
        private List<CandidatesBean> candidates;

        public TransactionBean getTransaction() {
            return transaction;
        }

        public void setTransaction(TransactionBean transaction) {
            this.transaction = transaction;
        }

        public List<CandidatesBean> getCandidates() {
            return candidates;
        }

        public void setCandidates(List<CandidatesBean> candidates) {
            this.candidates = candidates;
        }

        public static class TransactionBean {
            /**
             * status : success
             * width : 175
             * topLeftX : 103
             * topLeftY : 157
             * gallery_name : MyGallery
             * face_id : 1
             * confidence : 0.86944
             * subject_id : Elizabeth
             * height : 175
             * quality : 0.84705
             */

            private String status;
            private int width;
            private int topLeftX;
            private int topLeftY;
            private String gallery_name;
            private String face_id;
            private double confidence;
            private String subject_id;
            private int height;
            private double quality;

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public int getWidth() {
                return width;
            }

            public void setWidth(int width) {
                this.width = width;
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

            public String getGallery_name() {
                return gallery_name;
            }

            public void setGallery_name(String gallery_name) {
                this.gallery_name = gallery_name;
            }

            public String getFace_id() {
                return face_id;
            }

            public void setFace_id(String face_id) {
                this.face_id = face_id;
            }

            public double getConfidence() {
                return confidence;
            }

            public void setConfidence(double confidence) {
                this.confidence = confidence;
            }

            public String getSubject_id() {
                return subject_id;
            }

            public void setSubject_id(String subject_id) {
                this.subject_id = subject_id;
            }

            public int getHeight() {
                return height;
            }

            public void setHeight(int height) {
                this.height = height;
            }

            public double getQuality() {
                return quality;
            }

            public void setQuality(double quality) {
                this.quality = quality;
            }
        }

        public static class CandidatesBean {
            /**
             * subject_id : Elizabeth
             * confidence : 0.86944
             * face_id : 5a48b0c02ecdf7613802
             * enrollment_timestamp : 1486925605094
             */

            private String subject_id;
            private double confidence;
            private String face_id;
            private String enrollment_timestamp;

            public String getSubject_id() {
                return subject_id;
            }

            public void setSubject_id(String subject_id) {
                this.subject_id = subject_id;
            }

            public double getConfidence() {
                return confidence;
            }

            public void setConfidence(double confidence) {
                this.confidence = confidence;
            }

            public String getFace_id() {
                return face_id;
            }

            public void setFace_id(String face_id) {
                this.face_id = face_id;
            }

            public String getEnrollment_timestamp() {
                return enrollment_timestamp;
            }

            public void setEnrollment_timestamp(String enrollment_timestamp) {
                this.enrollment_timestamp = enrollment_timestamp;
            }
        }
    }
}
