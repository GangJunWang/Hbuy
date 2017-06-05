package au.com.hbuy.aotong.domain;

public class UserInfoBen {
    private String userId;
    private String userName;
    private String imageUrl;

    public UserInfoBen(String userId, String userName, String imageUrl) {
        this.userId = userId;
        this.userName = userName;
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }
}


