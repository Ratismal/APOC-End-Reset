package org.apocgaming.endreset;

import java.util.HashMap;

/**
 * Created by Haze on 12/29/2014.
 */
public abstract class HashMapManager<K,V> {


    public HashMap<K,V> contents;

    public final HashMap<K,V> getContents(){
        return contents;
    }

    public abstract void setup();

}
