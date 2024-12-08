package me.socialnetwork.post;

public class Post {

    private final String name;
    private final String username;
    private final String content;
    private final int avatarResource;

    public Post(String name, String username, String content, int avatarResource) {
        this.name = name;
        this.username = username;
        this.content = content;
        this.avatarResource = avatarResource;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }

    public int getAvatarResource() {
        return avatarResource;
    }

}

