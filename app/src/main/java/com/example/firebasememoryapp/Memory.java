package com.example.firebasememoryapp;

import android.os.Parcel;
import android.os.Parcelable;

public class Memory implements Parcelable {
    int rating;
    String name;
    String desc;
    int imageResourceId;

    public Memory(int rating, String name, String desc, int imageResourceId) {
        this.rating = rating;
        this.name = name;
        this.desc = desc;
        this.imageResourceId = imageResourceId;
    }

    public Memory(int rating, String name, String desc){
        this.rating = rating;
        this.name = name;
        this.desc = desc;
        imageResourceId = 0;
    }

    public Memory() {
        rating = 0;
        name = "";
        desc = "";
        imageResourceId = 0;

    }
    // this code is needed for the Food class to work with Parcelable
    public static final Parcelable.Creator<Memory> CREATOR = new
            Parcelable.Creator<Memory>() {

                @Override
                public Memory createFromParcel(Parcel parcel) {
                    return new Memory(parcel);
                }

                @Override
                public Memory[] newArray(int size) {
                    return new Memory[0];
                }
            };



    /** This is a "constructor" of sorts that is needed with the Parceable interface to
     * tell the intent how to create a Food object when it is received from the intent
     * basically it is setting each instance variable as a String or Int
     * if the instance variables were objects themselves you would need to do more complex * code.  We need to read in the String, double, and int data.
     *
     * @param parcel    the parcel that is received from the intent
     */

    public Memory(Parcel parcel) {
        name = parcel.readString();
        rating = parcel.readInt();
        desc = parcel.readString();
        imageResourceId = parcel.readInt();
    }

    /**
     * This is what is used when we send the Food object through an intent
     * It is also a method that is part of the Parceable interface and is needed
     * to set up the object that is being sent.  Then, when it is received, the
     * other Food constructor that accepts a Parcel reference can "unpack it"
     *
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(rating);
        dest.writeString(desc);
        dest.writeInt(imageResourceId);
    }
    public String toString() {
        return this.name;
    }





    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }

    /**
     * This method is required for the Parceable interface.  As of now, this method * is in the default state and doesn't really do anything.
     *
     * If your Parcelable class will have child classes, you'll need to
     * take some extra care with the describeContents() method. This would
     * let you identify the specific child class that should be created by
     * the Parcelable.Creator. You can read more about how this works on
     *  Stack Overflow with this link.
     *           https://stackoverflow.com/questions/4778834/purpose-of-describecontents-of-parcelable-interface
     * @return
     */

    @Override
    public int describeContents() {
        return 0;
    }

}
