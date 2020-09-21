package com.younoq.noqfuelstation.classes;

public class PetrolPump {

    private String id, name, address, city, pincode, state, country, phone_no, image;

    public PetrolPump(String id, String na, String addr, String cty, String pin, String st, String cntry, String p_no, String img) {

        this.id = id;
        this.name = na;
        this.address = addr;
        this.city = cty;
        this.pincode = pin;
        this.state = st;
        this.country = cntry;
        this.phone_no = p_no;
        this.image = img;

    }

    public String getId() { return id; }

    public String getName() { return name; }

    public String getAddress() { return address; }

    public String getCity() { return city; }

    public String getPincode() { return pincode; }

    public String getState() { return state; }

    public String getCountry() { return country; }

    public String getPhone_no() { return phone_no; }

    public String getImage() { return image; }
}
