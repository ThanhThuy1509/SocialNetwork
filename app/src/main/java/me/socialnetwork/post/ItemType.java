package me.socialnetwork.post;

import me.socialnetwork.R;

public enum ItemType {

    POST {
        public String like(String postId) {
            return String.format("/like/%s/like", postId);
        }
        public String unlike(String postId) {
            return String.format("/like/%s/unlike", postId);
        }

        @Override
        public String repost(String postId) {
            return String.format("/post/%s/repost", postId);
        }

        @Override
        public String unrepost(String postId) {
            return String.format("/post/%s/unrepost", postId);
        }

        @Override
        public int getLayout() {
            return R.layout.item_post;
        }
    },
    POST_2 {
        public String like(String postId) {
            return String.format("/like/%s/like", postId);
        }
        public String unlike(String postId) {
            return String.format("/like/%s/unlike", postId);
        }

        @Override
        public String repost(String postId) {
            return String.format("/post/%s/repost", postId);
        }

        @Override
        public String unrepost(String postId) {
            return String.format("/post/%s/unrepost", postId);
        }

        @Override
        public int getLayout() {
            return R.layout.item_post;
        }
    }
    ,
    COMMENT {
        public String like(String postId) {
            return String.format("/comment/like/%s", postId);
        }
        public String unlike(String postId) {
            return String.format("/comment/unlike/%s", postId);
        }

        @Override
        public String repost(String postId) {
            return "";
        }

        @Override
        public String unrepost(String postId) {
            return "";
        }

        @Override
        public int getLayout() {
            return R.layout.item_reply;
        }
    },
    REPLY_TO_COMMENT {
        public String like(String postId) {
            return String.format("/comment/like/%s", postId);
        }
        public String unlike(String postId) {
            return String.format("/comment/unlike/%s", postId);
        }

        @Override
        public String repost(String postId) {
            return "";
        }

        @Override
        public String unrepost(String postId) {
            return "";
        }

        @Override
        public int getLayout() {
            return R.layout.item_reply_2;
        }
    };

    public abstract String like(String postId);
    public abstract String unlike(String postId);
    public abstract String repost(String postId);
    public abstract String unrepost(String postId);

    public abstract int getLayout();

}
