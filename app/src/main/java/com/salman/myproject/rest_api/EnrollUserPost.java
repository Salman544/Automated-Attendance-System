package com.salman.myproject.rest_api;

import java.util.List;

/**
 * Created by Salman on 1/1/2018.
 */

public class EnrollUserPost {


    /**
     * face_id : 5410001743ab64935982
     * images : [{"attributes":{"lips":"Together","asian":0.25658,"gender":{"type":"F"},"age":26,"hispanic":0.41825,"other":0.11144,"black":0.16007,"white":0.05365,"glasses":"None"},"transaction":{"status":"success","topLeftX":390,"topLeftY":706,"gallery_name":"MyGallery","timestamp":"1487012582681","height":780,"quality":0.79333,"confidence":0.99997,"subject_id":"Elizabeth","width":781,"face_id":1}}]
     */

    private String face_id;
    private List<ImagesBean> images;

    public String getFace_id() {
        return face_id;
    }

    public void setFace_id(String face_id) {
        this.face_id = face_id;
    }

    public List<ImagesBean> getImages() {
        return images;
    }

    public void setImages(List<ImagesBean> images) {
        this.images = images;
    }

    public static class ImagesBean {
        /**
         * attributes : {"lips":"Together","asian":0.25658,"gender":{"type":"F"},"age":26,"hispanic":0.41825,"other":0.11144,"black":0.16007,"white":0.05365,"glasses":"None"}
         * transaction : {"status":"success","topLeftX":390,"topLeftY":706,"gallery_name":"MyGallery","timestamp":"1487012582681","height":780,"quality":0.79333,"confidence":0.99997,"subject_id":"Elizabeth","width":781,"face_id":1}
         */

        private AttributesBean attributes;
        private TransactionBean transaction;

        public AttributesBean getAttributes() {
            return attributes;
        }

        public void setAttributes(AttributesBean attributes) {
            this.attributes = attributes;
        }

        public TransactionBean getTransaction() {
            return transaction;
        }

        public void setTransaction(TransactionBean transaction) {
            this.transaction = transaction;
        }

        public static class AttributesBean {
            /**
             * lips : Together
             * asian : 0.25658
             * gender : {"type":"F"}
             * age : 26
             * hispanic : 0.41825
             * other : 0.11144
             * black : 0.16007
             * white : 0.05365
             * glasses : None
             */

            private String lips;
            private double asian;
            private GenderBean gender;
            private int age;
            private double hispanic;
            private double other;
            private double black;
            private double white;
            private String glasses;

            public String getLips() {
                return lips;
            }

            public void setLips(String lips) {
                this.lips = lips;
            }

            public double getAsian() {
                return asian;
            }

            public void setAsian(double asian) {
                this.asian = asian;
            }

            public GenderBean getGender() {
                return gender;
            }

            public void setGender(GenderBean gender) {
                this.gender = gender;
            }

            public int getAge() {
                return age;
            }

            public void setAge(int age) {
                this.age = age;
            }

            public double getHispanic() {
                return hispanic;
            }

            public void setHispanic(double hispanic) {
                this.hispanic = hispanic;
            }

            public double getOther() {
                return other;
            }

            public void setOther(double other) {
                this.other = other;
            }

            public double getBlack() {
                return black;
            }

            public void setBlack(double black) {
                this.black = black;
            }

            public double getWhite() {
                return white;
            }

            public void setWhite(double white) {
                this.white = white;
            }

            public String getGlasses() {
                return glasses;
            }

            public void setGlasses(String glasses) {
                this.glasses = glasses;
            }

            public static class GenderBean {
                /**
                 * type : F
                 */

                private String type;

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }
            }
        }

        public static class TransactionBean {
            /**
             * status : success
             * topLeftX : 390
             * topLeftY : 706
             * gallery_name : MyGallery
             * timestamp : 1487012582681
             * height : 780
             * quality : 0.79333
             * confidence : 0.99997
             * subject_id : Elizabeth
             * width : 781
             * face_id : 1
             */

            private String status;
            private int topLeftX;
            private int topLeftY;
            private String gallery_name;
            private String timestamp;
            private int height;
            private double quality;
            private double confidence;
            private String subject_id;
            private int width;
            private int face_id;

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
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

            public String getTimestamp() {
                return timestamp;
            }

            public void setTimestamp(String timestamp) {
                this.timestamp = timestamp;
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

            public int getWidth() {
                return width;
            }

            public void setWidth(int width) {
                this.width = width;
            }

            public int getFace_id() {
                return face_id;
            }

            public void setFace_id(int face_id) {
                this.face_id = face_id;
            }
        }
    }
}
