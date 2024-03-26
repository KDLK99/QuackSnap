package com.example.fase1_grupob.model;

public class Comment {

        private Long userId;
        private String username;
        private String text;

        public Comment() {

        }

        public Comment(Long userId, String text, String username) {
            super();
            this.userId = userId;
            this.text = text;
            this.username = username;
        }

        public Long getUserId() {
            return this.userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getText() {
            return this.text;
        }

        public String getUsername(){
            return this.username;
        }

        public void setUsername(String username){
            this.username = username;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String toString(){
            return "<strong>" + this.userId + ": "  + "</strong>"+ this.text;
        }

}
