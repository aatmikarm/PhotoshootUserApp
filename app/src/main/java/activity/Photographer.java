package activity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Photographer implements Parcelable{

    private String email;
    private String photographer_id;
    private String photographername;
    private String phone;
    private String password;
    private GeoPoint geo_point;
    private @ServerTimestamp Date timestamp;

    public Photographer(String email, String photographer_id, String photographername, String phone , String password , GeoPoint geo_point, Date timestamp) {
        this.email = email;
        this.photographer_id = photographer_id;
        this.photographername = photographername;
        this.phone = phone;
        this.password = password;
        this.geo_point = geo_point;
        this.timestamp = timestamp;
    }

    public Photographer() {

    }

    protected Photographer(Parcel in) {
        email = in.readString();
        photographer_id = in.readString();
        photographername = in.readString();
        phone = in.readString();
        password = in.readString();
    }

    public static final Creator<Photographer> CREATOR = new Creator<Photographer>() {
        @Override
        public Photographer createFromParcel(Parcel in) {
            return new Photographer(in);
        }

        @Override
        public Photographer[] newArray(int size) {
            return new Photographer[size];
        }
    };

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public static Creator<Photographer> getCREATOR() {
        return CREATOR;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotographer_id() {
        return photographer_id;
    }

    public void setPhotographer_id(String photographer_id) {
        this.photographer_id = photographer_id;
    }

    public String getPhotographername() {
        return photographername;
    }

    public void setPhotographername(String photographername) {
        this.photographername = photographername;
    }

    public GeoPoint getGeo_point() {
        return geo_point;
    }

    public void setGeo_point(GeoPoint geo_point) {
        this.geo_point = geo_point;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Photographer{" +
                "email='" + email + '\'' +
                ", photographer_id='" + photographer_id + '\'' +
                ", photographername='" + photographername + '\'' +
                ", phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                ", geo_point=" + geo_point +
                ", timestamp=" + timestamp +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(photographer_id);
        dest.writeString(photographername);
        dest.writeString(phone);
        dest.writeString(password);
    }
}