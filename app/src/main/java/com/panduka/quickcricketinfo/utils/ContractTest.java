package com.panduka.quickcricketinfo.utils;

import android.util.Log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by pandukadesilva on 3/2/16.
 */
public class ContractTest {

    public ContractTest() {

        Map<EmployeeKey, String> cache = loadEmployeeCache();

        EmployeeKey lookUpKey = new EmployeeKey("102", "12101984"); //test for hashmap duplicate keys

        String empName = cache.get(lookUpKey);

        if (empName != null)
            Log.d("Pandlk_HASHMAP_TEST", empName);
        else
            Log.d("Pandlk_HASHMAP_TEST", "Nothing will return if hashcode is not implemented");


        EmployeeKey lookUpKey1 = new EmployeeKey("101", "10101975");
        EmployeeKey lookUpKey2 = new EmployeeKey("101", "10101974");
        EmployeeKey lookUpKey3 = new EmployeeKey("102", "10101974");
        EmployeeKey lookUpKey4 = new EmployeeKey("102", "10101974");
        EmployeeKey lookUpKey5 = new EmployeeKey("103", "12101974");

        Set<EmployeeKey> set = new HashSet<>();

        set.add(lookUpKey1);
        set.add(lookUpKey2);
        set.add(lookUpKey3);
        set.add(lookUpKey4);
        set.add(lookUpKey5);

        Log.d("Pandlk_HASHSET_TEST",set.toString());

    }

    static Map<EmployeeKey, String> loadEmployeeCache() {
        EmployeeKey ek1 = new EmployeeKey("100", "10101984");
        EmployeeKey ek2 = new EmployeeKey("101", "11101984");
        EmployeeKey ek3 = new EmployeeKey("102", "12101984");
        EmployeeKey ek4 = new EmployeeKey("102", "12101984");
        EmployeeKey ek5 = new EmployeeKey("102", "12101984");

        Map<EmployeeKey, String> cache = new HashMap<>();

        cache.put(ek1, "Bob");
        cache.put(ek2, "Steve");
        cache.put(ek3, "Robert");
        cache.put(ek4, "Panduka");
        cache.put(ek5, "Umanga");

        return cache;
    }
}



class EmployeeKey {
    String empId;
    String dob;

    public EmployeeKey(String theId, String theDob) {
        empId = theId;
        dob = theDob;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmployeeKey that = (EmployeeKey) o;

        if (empId != null ? !empId.equals(that.empId) : that.empId != null) return false;
        return !(dob != null ? !dob.equals(that.dob) : that.dob != null);

    }

    @Override
    public int hashCode() {
        int result = empId != null ? empId.hashCode() : 0;
        result = 31 * result + (dob != null ? dob.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EmployeeKey{" +
                "empId='" + empId + '\'' +
                ", dob='" + dob + '\'' +
                '}';
    }
}