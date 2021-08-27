package activity;

public class proDetail_RV_Model {
    String imageUrl;
    String uid;
    String currentDateandTime;

    public String getCurrentDateandTime() {
        return currentDateandTime;
    }

    public void setCurrentDateandTime(String currentDateandTime) {
        this.currentDateandTime = currentDateandTime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}