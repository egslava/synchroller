package ru.egslava.synchroller;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

public class SavedState extends View.BaseSavedState {

        public int  offsetX, offsetY;

        SavedState(Parcelable superstate){
            super(superstate);
        }

        SavedState(Parcel source) {
            super(source);
            offsetX          = source.readInt();
            offsetY          = source.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(offsetX);
            dest.writeInt(offsetY);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }