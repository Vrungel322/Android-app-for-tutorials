package com.nanddgroup.contactmanager;

import android.net.Uri;

/**
 * Created by Nikita on 08.11.2015.
 */
public class Contact {

    private String _name, _phone, _email, _address;
    private Uri _imageURI;
    private int _id;

    public Contact(int id, String name, String phone, String email, String address, Uri imageURI) {
        _id = id;
        _name = name;
        _phone = phone;
        _email = email;
        _address = address;
        _imageURI = imageURI;
    }

    public int get_id() {
        return _id;
    }

    public Uri get_imageURI() {
        return _imageURI;
    }

    public String get_address() {
        return _address;
    }

    public String get_email() {
        return _email;
    }

    public String get_name() {
        return _name;
    }

    public String get_phone() {
        return _phone;
    }
}
