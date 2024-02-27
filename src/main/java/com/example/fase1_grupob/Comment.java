package com.example.fase1_grupob;

public class Comment {

        private String user;
        private String text;

        public Comment() {

        }

        public Comment(String user, String text) {
            super();
            this.user = user;
            this.text = text;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }



        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

}
