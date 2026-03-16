package com.dby.user.model;

/**
 * 用户响应 DTO（不含密码）
 */
public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private String nickname;
    private boolean enabled;

    public UserResponse() {}

    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.enabled = user.isEnabled();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
