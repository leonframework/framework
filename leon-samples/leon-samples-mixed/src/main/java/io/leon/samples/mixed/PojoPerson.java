/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.samples.mixed;

import java.util.ArrayList;
import java.util.List;

public class PojoPerson {

    private String firstName;
    private String lastName;
    private PojoAddress address;
    private List<String> hobbies;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public PojoAddress getAddress() {
        return address;
    }

    public void setAddress(PojoAddress address) {
        this.address = address;
    }

    public List<String> getHobbies() {
        if(hobbies == null)
            hobbies = new ArrayList<String>();

        return hobbies;
    }

    public void setHobbies(List<String> hobbies) {
        this.hobbies = hobbies;
    }

    @Override
    public String toString() {
        return "PojoPerson{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", address=" + address +
                ", hobbies=" + hobbies +
                '}';
    }
}