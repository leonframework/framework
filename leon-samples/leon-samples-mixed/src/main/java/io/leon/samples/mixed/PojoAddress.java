/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.samples.mixed;

public class PojoAddress {

    private String zipcode;
    private String city;
    private PojoCountry country;

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public PojoCountry getCountry() {
        return country;
    }

    public void setCountry(PojoCountry country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "PojoAddress{" +
                "zipcode='" + zipcode + '\'' +
                ", city='" + city + '\'' +
                ", country=" + country +
                '}';
    }
}