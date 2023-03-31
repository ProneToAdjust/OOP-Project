package com.projectdemo;

import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

public class DataStorageTest {
    @Test
    public void loadDatabaseTest(){
        Bank DB = DataStorage.loadDatabase();
        assertNotEquals(DB, null);
    }
}