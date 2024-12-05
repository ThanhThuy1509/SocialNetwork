package me.socialnetwork;

// Post.java
public class Post {
    private String username;
    private String content;
    private int avatarResource;

    public Post(String username, String content, int avatarResource) {
        this.username = username;
        this.content = content;
        this.avatarResource = avatarResource;
    }

    public String getUsername() { return username; }
    public String getContent() { return content; }
    public int getAvatarResource() { return avatarResource; }
}

