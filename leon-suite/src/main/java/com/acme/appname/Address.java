package com.acme.appname;

public class Address {
    private String nam;
    private String tel;
    private String mail;

    public String getNam() {
        return nam;
    }

    public void setNam(String nam) {
        this.nam = nam;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    @Override
    public String toString() {
        return "Address{" +
                "nam='" + nam + '\'' +
                ", tel='" + tel + '\'' +
                ", mail='" + mail + '\'' +
                '}';
    }
}
