package me.socialnetwork.post;

import com.google.gson.annotations.SerializedName;

public class Post {

    private String name;
    private String username;
    private String content;
    private String avatar;

    private boolean following;

    @SerializedName("_id")
    private String id;

    @SerializedName("user_id")
    private String userID;

    @SerializedName("create_time")
    private String createTime;

    @SerializedName("is_like")
    private boolean isLike;

    @SerializedName("like_count")
    private String likeCount;

    @SerializedName("comment_count")
    private String repliedCount;

    @SerializedName("is_reposted")
    private Boolean isReposted;
    @SerializedName("repost_count")
    private String repostCount;

    public Post(String content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public boolean getLiked() {
        return isLike;
    }

    public void setLike(boolean like) {
        this.isLike = like;
    }

    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }

    public String getRepliedCount() {
        return repliedCount;
    }


    public void setRepliedCount(String repliedCount) {
        this.repliedCount = repliedCount;
    }

    public String getRepostCount() {
        return repostCount;
    }

    public void setRepostCount(String repostCount) {
        this.repostCount = repostCount;
    }

    public Boolean getReposted() {
        return isReposted;
    }

    public void setReposted(Boolean reposted) {
        isReposted = reposted;
    }
}