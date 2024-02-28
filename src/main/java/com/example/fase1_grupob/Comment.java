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
            return this.user;
        }

        public void setUser(String user) {
            this.user = user;
        }



        public String getText() {
            return this.text;
        }

        public void setText(String text) {
            this.text = text;
        }


        public String toString(){
            return "<strong>" + this.user + ": "  + "</strong>"+ this.text;
        }

}
